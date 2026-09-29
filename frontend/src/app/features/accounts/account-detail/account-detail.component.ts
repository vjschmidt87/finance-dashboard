import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AccountService } from '../../../core/services/account.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { Account } from '../../../core/models/account.model';
import { Transaction } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-account-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './account-detail.component.html',
  styleUrls: ['./account-detail.component.scss']
})
export class AccountDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private accountService = inject(AccountService);
  private transactionService = inject(TransactionService);
  private destroyRef = inject(DestroyRef);

  account: Account | null = null;
  transactions: Transaction[] = [];
  isLoading = true;
  errorMessage = '';

  ngOnInit(): void {
    this.route.params.pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(params => {
        const id = +params['id'];
        return this.accountService.getAccount(id);
      })
    ).subscribe({
      next: (account) => {
        this.account = account;
        this.loadTransactions(account.id);
      },
      error: () => {
        this.errorMessage = 'Failed to load account.';
        this.isLoading = false;
      }
    });
  }

  private loadTransactions(accountId: number): void {
    this.transactionService.getTransactions({ accountId, size: 50 }).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (response) => { this.transactions = response.content; this.isLoading = false; },
      error: () => { this.errorMessage = 'Failed to load transactions.'; this.isLoading = false; }
    });
  }

  trackByTransaction(_index: number, tx: Transaction): number {
    return tx.id;
  }
}
