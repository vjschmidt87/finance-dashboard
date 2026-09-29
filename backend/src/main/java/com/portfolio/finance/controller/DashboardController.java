package com.portfolio.finance.controller;

import com.portfolio.finance.dto.DashboardDto;
import com.portfolio.finance.security.SecurityUtils;
import com.portfolio.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getDashboard(userId, month, year));
    }
}
