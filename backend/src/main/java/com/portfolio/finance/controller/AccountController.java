package com.portfolio.finance.controller;

import com.portfolio.finance.dto.AccountDto;
import com.portfolio.finance.security.SecurityUtils;
import com.portfolio.finance.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(accountService.getAccountById(id, userId));
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto accountDto) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountDto, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDto accountDto) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(accountService.updateAccount(id, accountDto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        accountService.deleteAccount(id, userId);
        return ResponseEntity.noContent().build();
    }
}
