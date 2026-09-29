import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BudgetProgress } from '../../../../core/models/budget.model';

@Component({
  selector: 'app-budget-progress',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './budget-progress.component.html',
  styleUrls: ['./budget-progress.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetProgressComponent {
  @Input() budgets: BudgetProgress[] = [];

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
