package com.portfolio.finance.scheduler;

import com.portfolio.finance.entity.RecurringTransaction;
import com.portfolio.finance.entity.Transaction;
import com.portfolio.finance.repository.RecurringTransactionRepository;
import com.portfolio.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionScheduler {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void processRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueTransactions =
                recurringTransactionRepository.findByActiveTrueAndNextExecutionDateLessThanEqual(today);

        log.info("Processing {} recurring transactions", dueTransactions.size());

        for (RecurringTransaction recurring : dueTransactions) {
            try {
                Transaction transaction = Transaction.builder()
                        .amount(recurring.getAmount())
                        .description(recurring.getDescription())
                        .date(recurring.getNextExecutionDate())
                        .type(recurring.getType())
                        .account(recurring.getAccount())
                        .category(recurring.getCategory())
                        .user(recurring.getUser())
                        .build();

                transactionRepository.save(transaction);

                recurring.setNextExecutionDate(calculateNextDate(recurring));
                recurringTransactionRepository.save(recurring);

                log.info("Processed recurring transaction: {} for user: {}",
                        recurring.getDescription(), recurring.getUser().getEmail());
            } catch (Exception e) {
                log.error("Failed to process recurring transaction id={}: {}",
                        recurring.getId(), e.getMessage(), e);
            }
        }
    }

    private LocalDate calculateNextDate(RecurringTransaction recurring) {
        LocalDate current = recurring.getNextExecutionDate();
        return switch (recurring.getFrequency()) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }
}
