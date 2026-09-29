import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, switchMap, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BudgetService } from '../../../core/services/budget.service';
import { BudgetProgress } from '../../../core/models/budget.model';

@Component({
  selector: 'app-budget-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './budget-list.component.html',
  styleUrls: ['./budget-list.component.scss']
})
export class BudgetListComponent implements OnInit {
  private budgetService = inject(BudgetService);
  private destroyRef = inject(DestroyRef);

  budgets: BudgetProgress[] = [];
  isLoading = true;
  errorMessage = '';

  selectedMonth: number;
  selectedYear: number;

  months = [
    { value: 1, label: 'January' }, { value: 2, label: 'February' },
    { value: 3, label: 'March' }, { value: 4, label: 'April' },
    { value: 5, label: 'May' }, { value: 6, label: 'June' },
    { value: 7, label: 'July' }, { value: 8, label: 'August' },
    { value: 9, label: 'September' }, { value: 10, label: 'October' },
    { value: 11, label: 'November' }, { value: 12, label: 'December' }
  ];
  years: number[] = [];

  private period$ = new BehaviorSubject<{ month: number; year: number }>({
    month: new Date().getMonth() + 1,
    year: new Date().getFullYear()
  });

  constructor() {
    const currentYear = new Date().getFullYear();
    this.years = Array.from({ length: 5 }, (_, i) => currentYear - 2 + i);
    this.selectedMonth = new Date().getMonth() + 1;
    this.selectedYear = currentYear;
  }

  ngOnInit(): void {
    this.period$.pipe(
      takeUntilDestroyed(this.destroyRef),
      tap(() => { this.isLoading = true; this.errorMessage = ''; }),
      switchMap(({ month, year }) => this.budgetService.getBudgets(month, year))
    ).subscribe({
      next: (budgets) => { this.budgets = budgets; this.isLoading = false; },
      error: () => { this.errorMessage = 'Failed to load budgets.'; this.isLoading = false; }
    });
  }

  onPeriodChange(): void {
    this.period$.next({ month: this.selectedMonth, year: this.selectedYear });
  }

  deleteBudget(id: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Delete this budget?')) {
      this.budgetService.deleteBudget(id).pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe({
        next: () => { this.budgets = this.budgets.filter(b => b.id !== id); },
        error: () => { this.errorMessage = 'Failed to delete budget.'; }
      });
    }
  }

  getProgressColor(percentUsed: number): string {
    if (percentUsed >= 90) return 'var(--danger)';
    if (percentUsed >= 75) return 'var(--warning)';
    return 'var(--success)';
  }

  getProgressWidth(percentUsed: number): string {
    return Math.min(percentUsed, 100) + '%';
  }

  trackByBudget(_index: number, budget: BudgetProgress): number {
    return budget.id;
  }
}
