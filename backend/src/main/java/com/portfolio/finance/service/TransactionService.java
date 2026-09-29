package com.portfolio.finance.service;

import com.portfolio.finance.dto.CategoryBreakdownDto;
import com.portfolio.finance.dto.MonthlySummaryDto;
import com.portfolio.finance.dto.TransactionDto;
import com.portfolio.finance.entity.Account;
import com.portfolio.finance.entity.Category;
import com.portfolio.finance.entity.Transaction;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.repository.AccountRepository;
import com.portfolio.finance.repository.CategoryRepository;
import com.portfolio.finance.repository.TransactionRepository;
import com.portfolio.finance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactions(Long userId, LocalDate dateFrom, LocalDate dateTo,
                                                 Long accountId, Long categoryId, Pageable pageable) {
        return transactionRepository.findByFilters(userId, dateFrom, dateTo, accountId, categoryId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public TransactionDto getTransactionById(Long id, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        return toDto(transaction);
    }

    @Transactional
    public TransactionDto createTransaction(TransactionDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Account account = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        }

        Transaction transaction = Transaction.builder()
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .date(dto.getDate())
                .type(dto.getType())
                .account(account)
                .category(category)
                .user(user)
                .build();

        updateAccountBalance(account, dto.getType(), dto.getAmount());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Created transaction id={} (type={}, amount={}) for user {}",
                saved.getId(), saved.getType(), saved.getAmount(), userId);
        return toDto(saved);
    }

    @Transactional
    public TransactionDto updateTransaction(Long id, TransactionDto dto, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        Account oldAccount = transaction.getAccount();
        reverseAccountBalance(oldAccount, transaction.getType(), transaction.getAmount());

        Account account = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        }

        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setDate(dto.getDate());
        transaction.setType(dto.getType());
        transaction.setAccount(account);
        transaction.setCategory(category);

        updateAccountBalance(account, dto.getType(), dto.getAmount());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Updated transaction id={} for user {}", id, userId);
        return toDto(saved);
    }

    @Transactional
    public void deleteTransaction(Long id, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        reverseAccountBalance(transaction.getAccount(), transaction.getType(), transaction.getAmount());
        transactionRepository.delete(transaction);
        log.info("Deleted transaction id={} for user {}", id, userId);
    }

    @Transactional(readOnly = true)
    public MonthlySummaryDto getMonthlySummary(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Object[]> totals = transactionRepository.getMonthlyTotalsByType(userId, start, end);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Object[] row : totals) {
            Transaction.TransactionType type = (Transaction.TransactionType) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            if (type == Transaction.TransactionType.INCOME) {
                totalIncome = amount;
            } else if (type == Transaction.TransactionType.EXPENSE) {
                totalExpenses = amount;
            }
        }

        return MonthlySummaryDto.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(totalIncome.subtract(totalExpenses))
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryBreakdownDto> getCategoryBreakdown(Long userId, LocalDate dateFrom, LocalDate dateTo) {
        List<Object[]> results = transactionRepository.getCategoryBreakdown(
                userId, dateFrom, dateTo, Transaction.TransactionType.EXPENSE);

        BigDecimal total = results.stream()
                .map(row -> (BigDecimal) row[2])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
                .map(row -> {
                    BigDecimal amount = (BigDecimal) row[2];
                    double percentage = total.compareTo(BigDecimal.ZERO) > 0
                            ? amount.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;

                    return CategoryBreakdownDto.builder()
                            .categoryName((String) row[0])
                            .categoryIcon((String) row[1])
                            .totalAmount(amount)
                            .percentage(percentage)
                            .transactionCount((Long) row[3])
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getRecentTransactions(Long userId, int limit) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId, Pageable.ofSize(limit))
                .map(this::toDto)
                .getContent();
    }

    private void updateAccountBalance(Account account, Transaction.TransactionType type, BigDecimal amount) {
        if (type == Transaction.TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else if (type == Transaction.TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(amount));
        }
        accountRepository.save(account);
    }

    private void reverseAccountBalance(Account account, Transaction.TransactionType type, BigDecimal amount) {
        if (type == Transaction.TransactionType.INCOME) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if (type == Transaction.TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().add(amount));
        }
        accountRepository.save(account);
    }

    private TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .type(transaction.getType())
                .accountId(transaction.getAccount().getId())
                .accountName(transaction.getAccount().getName())
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
                .build();
    }
}
