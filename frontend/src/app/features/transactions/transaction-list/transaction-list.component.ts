import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, combineLatest, debounceTime, distinctUntilChanged, switchMap, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountService } from '../../../core/services/account.service';
import { CategoryService } from '../../../core/services/category.service';
import { Transaction, PagedResponse } from '../../../core/models/transaction.model';
import { Account } from '../../../core/models/account.model';
import { Category } from '../../../core/models/category.model';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.scss']
})
export class TransactionListComponent implements OnInit {
  private transactionService = inject(TransactionService);
  private accountService = inject(AccountService);
  private categoryService = inject(CategoryService);
  private destroyRef = inject(DestroyRef);

  transactions: Transaction[] = [];
  accounts: Account[] = [];
  categories: Category[] = [];
  isLoading = true;
  errorMessage = '';
  totalElements = 0;
  totalPages = 0;

  dateFrom$ = new BehaviorSubject<string>('');
  dateTo$ = new BehaviorSubject<string>('');
  accountId$ = new BehaviorSubject<number | undefined>(undefined);
  categoryId$ = new BehaviorSubject<number | undefined>(undefined);
  page$ = new BehaviorSubject<number>(0);
  pageSize = 15;

  // Template-bound filter values
  dateFrom = '';
  dateTo = '';
  selectedAccountId: number | undefined;
  selectedCategoryId: number | undefined;

  ngOnInit(): void {
    this.accountService.getAccounts().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(a => this.accounts = a);

    this.categoryService.getCategories().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(c => this.categories = c);

    combineLatest([
      this.dateFrom$.pipe(debounceTime(300), distinctUntilChanged()),
      this.dateTo$.pipe(debounceTime(300), distinctUntilChanged()),
      this.accountId$.pipe(distinctUntilChanged()),
      this.categoryId$.pipe(distinctUntilChanged()),
      this.page$.pipe(distinctUntilChanged())
    ]).pipe(
      takeUntilDestroyed(this.destroyRef),
      tap(() => this.isLoading = true),
      switchMap(([dateFrom, dateTo, accountId, categoryId, page]) =>
        this.transactionService.getTransactions({
          dateFrom: dateFrom || undefined,
          dateTo: dateTo || undefined,
          accountId,
          categoryId,
          page,
          size: this.pageSize
        })
      )
    ).subscribe({
      next: (response: PagedResponse<Transaction>) => {
        this.transactions = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load transactions.';
        this.isLoading = false;
      }
    });
  }

  onDateFromChange(value: string): void { this.dateFrom = value; this.dateFrom$.next(value); this.page$.next(0); }
  onDateToChange(value: string): void { this.dateTo = value; this.dateTo$.next(value); this.page$.next(0); }
  onAccountChange(value: string): void {
    this.selectedAccountId = value ? +value : undefined;
    this.accountId$.next(this.selectedAccountId);
    this.page$.next(0);
  }
  onCategoryChange(value: string): void {
    this.selectedCategoryId = value ? +value : undefined;
    this.categoryId$.next(this.selectedCategoryId);
    this.page$.next(0);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) this.page$.next(page);
  }

  get currentPage(): number { return this.page$.value; }

  deleteTransaction(id: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Delete this transaction?')) {
      this.transactionService.deleteTransaction(id).pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe({
        next: () => { this.page$.next(this.page$.value); },
        error: () => { this.errorMessage = 'Failed to delete transaction.'; }
      });
    }
  }

  trackByTransaction(_index: number, tx: Transaction): number {
    return tx.id;
  }
}
