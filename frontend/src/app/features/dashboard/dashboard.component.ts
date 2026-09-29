import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, switchMap, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardData } from '../../core/models/dashboard.model';
import { StatCardComponent } from './components/stat-card/stat-card.component';
import { CategoryBreakdownChartComponent } from './components/category-breakdown-chart/category-breakdown-chart.component';
import { MonthlyTrendChartComponent } from './components/monthly-trend-chart/monthly-trend-chart.component';
import { BudgetProgressComponent } from './components/budget-progress/budget-progress.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterModule,
    StatCardComponent, CategoryBreakdownChartComponent,
    MonthlyTrendChartComponent, BudgetProgressComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private destroyRef = inject(DestroyRef);

  selectedPeriod$ = new BehaviorSubject<{ month: number; year: number }>({
    month: new Date().getMonth() + 1,
    year: new Date().getFullYear()
  });

  dashboardData: DashboardData | null = null;
  isLoading = true;
  errorMessage = '';

  months = [
    { value: 1, label: 'January' }, { value: 2, label: 'February' },
    { value: 3, label: 'March' }, { value: 4, label: 'April' },
    { value: 5, label: 'May' }, { value: 6, label: 'June' },
    { value: 7, label: 'July' }, { value: 8, label: 'August' },
    { value: 9, label: 'September' }, { value: 10, label: 'October' },
    { value: 11, label: 'November' }, { value: 12, label: 'December' }
  ];

  years: number[] = [];
  selectedMonth: number;
  selectedYear: number;

  constructor() {
    const currentYear = new Date().getFullYear();
    this.years = Array.from({ length: 5 }, (_, i) => currentYear - 2 + i);
    this.selectedMonth = new Date().getMonth() + 1;
    this.selectedYear = currentYear;
  }

  ngOnInit(): void {
    this.selectedPeriod$.pipe(
      takeUntilDestroyed(this.destroyRef),
      tap(() => { this.isLoading = true; this.errorMessage = ''; }),
      switchMap(({ month, year }) => this.dashboardService.getDashboard(month, year))
    ).subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load dashboard data.';
        this.isLoading = false;
      }
    });
  }

  onPeriodChange(): void {
    this.selectedPeriod$.next({ month: this.selectedMonth, year: this.selectedYear });
  }

  trackByTransaction(_index: number, tx: { id: number }): number {
    return tx.id;
  }
}
