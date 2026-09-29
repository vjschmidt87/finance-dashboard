package com.portfolio.finance.service;

import com.portfolio.finance.dto.AccountDto;
import com.portfolio.finance.entity.Account;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.repository.AccountRepository;
import com.portfolio.finance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id, Long userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        return toDto(account);
    }

    @Transactional
    public AccountDto createAccount(AccountDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Account account = Account.builder()
                .name(dto.getName())
                .type(dto.getType())
                .balance(dto.getBalance())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "USD")
                .user(user)
                .build();

        Account saved = accountRepository.save(account);
        log.info("Created account '{}' (id={}) for user {}", saved.getName(), saved.getId(), userId);
        return toDto(saved);
    }

    @Transactional
    public AccountDto updateAccount(Long id, AccountDto dto, Long userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.setName(dto.getName());
        account.setType(dto.getType());
        account.setBalance(dto.getBalance());
        if (dto.getCurrency() != null) {
            account.setCurrency(dto.getCurrency());
        }

        Account saved = accountRepository.save(account);
        log.info("Updated account id={} for user {}", id, userId);
        return toDto(saved);
    }

    @Transactional
    public void deleteAccount(Long id, Long userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        accountRepository.delete(account);
        log.info("Deleted account id={} for user {}", id, userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance(Long userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
