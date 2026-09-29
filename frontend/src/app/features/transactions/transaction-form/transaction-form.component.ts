import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { switchMap, of } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountService } from '../../../core/services/account.service';
import { CategoryService } from '../../../core/services/category.service';
import { Account } from '../../../core/models/account.model';
import { Category } from '../../../core/models/category.model';
import { TransactionType } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './transaction-form.component.html',
  styleUrls: ['./transaction-form.component.scss']
})
export class TransactionFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private transactionService = inject(TransactionService);
  private accountService = inject(AccountService);
  private categoryService = inject(CategoryService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  transactionForm: FormGroup = this.fb.group({
    amount: [null, [Validators.required, Validators.min(0.01)]],
    description: ['', Validators.required],
    date: ['', Validators.required],
    type: ['EXPENSE' as TransactionType, Validators.required],
    accountId: [null, Validators.required],
    categoryId: [null, Validators.required]
  });

  accounts: Account[] = [];
  categories: Category[] = [];
  filteredCategories: Category[] = [];
  isEditing = false;
  transactionId: number | null = null;
  isLoading = false;
  errorMessage = '';

  transactionTypes: { value: TransactionType; label: string }[] = [
    { value: 'INCOME', label: 'Income' },
    { value: 'EXPENSE', label: 'Expense' },
    { value: 'TRANSFER', label: 'Transfer' }
  ];

  ngOnInit(): void {
    this.accountService.getAccounts().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(a => this.accounts = a);

    this.categoryService.getCategories().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(c => {
      this.categories = c;
      this.filterCategories();
    });

    this.transactionForm.get('type')?.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.filterCategories());

    this.route.params.pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(params => {
        if (params['id']) {
          this.isEditing = true;
          this.transactionId = +params['id'];
          return this.transactionService.getTransaction(this.transactionId);
        }
        return of(null);
      })
    ).subscribe({
      next: (transaction) => {
        if (transaction) {
          this.transactionForm.patchValue({
            amount: transaction.amount,
            description: transaction.description,
            date: transaction.date,
            type: transaction.type,
            accountId: transaction.accountId,
            categoryId: transaction.categoryId
          });
        }
      },
      error: () => { this.errorMessage = 'Failed to load transaction.'; }
    });
  }

  filterCategories(): void {
    const type = this.transactionForm.get('type')?.value;
    if (type === 'TRANSFER') {
      this.filteredCategories = this.categories;
    } else {
      this.filteredCategories = this.categories.filter(c => c.type === type);
    }
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) { this.transactionForm.markAllAsTouched(); return; }
    this.isLoading = true;
    this.errorMessage = '';
    const data = this.transactionForm.value;

    const request$ = this.isEditing && this.transactionId
      ? this.transactionService.updateTransaction(this.transactionId, data)
      : this.transactionService.createTransaction(data);

    request$.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: () => { this.router.navigate(['/transactions']); },
      error: () => { this.errorMessage = 'Failed to save transaction.'; this.isLoading = false; }
    });
  }
}
