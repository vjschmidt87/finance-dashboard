package com.portfolio.finance.repository;

import com.portfolio.finance.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate dateFrom, LocalDate dateTo);

    Page<Transaction> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);

    List<Transaction> findByAccountId(Long accountId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t.type, SUM(t.amount) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.date BETWEEN :dateFrom AND :dateTo " +
           "GROUP BY t.type")
    List<Object[]> getMonthlyTotalsByType(@Param("userId") Long userId,
                                          @Param("dateFrom") LocalDate dateFrom,
                                          @Param("dateTo") LocalDate dateTo);

    @Query("SELECT t.category.name, t.category.icon, SUM(t.amount), COUNT(t) " +
           "FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.date BETWEEN :dateFrom AND :dateTo " +
           "AND t.type = :type AND t.category IS NOT NULL " +
           "GROUP BY t.category.name, t.category.icon " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategoryBreakdown(@Param("userId") Long userId,
                                        @Param("dateFrom") LocalDate dateFrom,
                                        @Param("dateTo") LocalDate dateTo,
                                        @Param("type") Transaction.TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.category.id = :categoryId " +
           "AND t.type = 'EXPENSE' " +
           "AND t.date BETWEEN :dateFrom AND :dateTo")
    BigDecimal sumByCategoryAndDateRange(@Param("userId") Long userId,
                                         @Param("categoryId") Long categoryId,
                                         @Param("dateFrom") LocalDate dateFrom,
                                         @Param("dateTo") LocalDate dateTo);

    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND (:dateFrom IS NULL OR t.date >= :dateFrom) " +
           "AND (:dateTo IS NULL OR t.date <= :dateTo) " +
           "AND (:accountId IS NULL OR t.account.id = :accountId) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "ORDER BY t.date DESC")
    Page<Transaction> findByFilters(@Param("userId") Long userId,
                                    @Param("dateFrom") LocalDate dateFrom,
                                    @Param("dateTo") LocalDate dateTo,
                                    @Param("accountId") Long accountId,
                                    @Param("categoryId") Long categoryId,
                                    Pageable pageable);
}
