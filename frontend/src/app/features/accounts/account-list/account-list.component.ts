import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AccountService } from '../../../core/services/account.service';
import { Account, AccountType } from '../../../core/models/account.model';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit {
  private accountService = inject(AccountService);
  private fb = inject(FormBuilder);
  private destroyRef = inject(DestroyRef);

  accounts: Account[] = [];
  isLoading = true;
  showForm = false;
  errorMessage = '';

  accountForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    type: ['CHECKING' as AccountType, Validators.required],
    balance: [0, [Validators.required, Validators.min(0)]],
    currency: ['USD', Validators.required]
  });

  accountTypes: { value: AccountType; label: string }[] = [
    { value: 'CHECKING', label: 'Checking' },
    { value: 'SAVINGS', label: 'Savings' },
    { value: 'CREDIT_CARD', label: 'Credit Card' },
    { value: 'INVESTMENT', label: 'Investment' },
    { value: 'CASH', label: 'Cash' }
  ];

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.accountService.getAccounts().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (accounts) => { this.accounts = accounts; this.isLoading = false; },
      error: () => { this.errorMessage = 'Failed to load accounts.'; this.isLoading = false; }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.accountForm.reset({ type: 'CHECKING', balance: 0, currency: 'USD' });
  }

  onSubmit(): void {
    if (this.accountForm.invalid) { this.accountForm.markAllAsTouched(); return; }
    this.accountService.createAccount(this.accountForm.value).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (account) => { this.accounts.push(account); this.toggleForm(); },
      error: () => { this.errorMessage = 'Failed to create account.'; }
    });
  }

  deleteAccount(id: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Are you sure you want to delete this account?')) {
      this.accountService.deleteAccount(id).pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe({
        next: () => { this.accounts = this.accounts.filter(a => a.id !== id); },
        error: () => { this.errorMessage = 'Failed to delete account.'; }
      });
    }
  }

  getTotalBalance(): number {
    return this.accounts.reduce((sum, a) => sum + a.balance, 0);
  }

  getAccountTypeIcon(type: AccountType): string {
    const icons: Record<AccountType, string> = {
      CHECKING: 'bank', SAVINGS: 'piggy-bank', CREDIT_CARD: 'credit-card',
      INVESTMENT: 'chart-line', CASH: 'wallet'
    };
    return icons[type] || 'wallet';
  }

  trackByAccount(_index: number, account: Account): number {
    return account.id;
  }
}
