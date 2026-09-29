import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'accounts',
    loadComponent: () => import('./features/accounts/account-list/account-list.component').then(m => m.AccountListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'accounts/:id',
    loadComponent: () => import('./features/accounts/account-detail/account-detail.component').then(m => m.AccountDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: 'transactions',
    loadComponent: () => import('./features/transactions/transaction-list/transaction-list.component').then(m => m.TransactionListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'transactions/new',
    loadComponent: () => import('./features/transactions/transaction-form/transaction-form.component').then(m => m.TransactionFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'transactions/:id/edit',
    loadComponent: () => import('./features/transactions/transaction-form/transaction-form.component').then(m => m.TransactionFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'budgets',
    loadComponent: () => import('./features/budgets/budget-list/budget-list.component').then(m => m.BudgetListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'budgets/new',
    loadComponent: () => import('./features/budgets/budget-form/budget-form.component').then(m => m.BudgetFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'budgets/:id/edit',
    loadComponent: () => import('./features/budgets/budget-form/budget-form.component').then(m => m.BudgetFormComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
