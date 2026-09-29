import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { switchMap, of } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BudgetService } from '../../../core/services/budget.service';
import { CategoryService } from '../../../core/services/category.service';
import { Category } from '../../../core/models/category.model';

@Component({
  selector: 'app-budget-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './budget-form.component.html',
  styleUrls: ['./budget-form.component.scss']
})
export class BudgetFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private budgetService = inject(BudgetService);
  private categoryService = inject(CategoryService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  budgetForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    limitAmount: [null, [Validators.required, Validators.min(1)]],
    categoryId: [null, Validators.required],
    month: [new Date().getMonth() + 1, Validators.required],
    year: [new Date().getFullYear(), Validators.required]
  });

  expenseCategories: Category[] = [];
  isEditing = false;
  budgetId: number | null = null;
  isLoading = false;
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

  constructor() {
    const currentYear = new Date().getFullYear();
    this.years = Array.from({ length: 5 }, (_, i) => currentYear - 2 + i);
  }

  ngOnInit(): void {
    this.categoryService.getExpenseCategories().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(c => this.expenseCategories = c);

    this.route.params.pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(params => {
        if (params['id']) {
          this.isEditing = true;
          this.budgetId = +params['id'];
          return this.budgetService.getBudgetProgress(this.budgetId);
        }
        return of(null);
      })
    ).subscribe({
      next: (budget) => {
        if (budget) {
          this.budgetForm.patchValue({
            name: budget.name,
            limitAmount: budget.limitAmount,
            categoryId: budget.categoryId,
            month: budget.month,
            year: budget.year
          });
        }
      },
      error: () => { this.errorMessage = 'Failed to load budget.'; }
    });
  }

  onSubmit(): void {
    if (this.budgetForm.invalid) { this.budgetForm.markAllAsTouched(); return; }
    this.isLoading = true;
    this.errorMessage = '';
    const data = this.budgetForm.value;

    const request$ = this.isEditing && this.budgetId
      ? this.budgetService.updateBudget(this.budgetId, data)
      : this.budgetService.createBudget(data);

    request$.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: () => { this.router.navigate(['/budgets']); },
      error: () => { this.errorMessage = 'Failed to save budget.'; this.isLoading = false; }
    });
  }
}
