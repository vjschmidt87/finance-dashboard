import { Transaction } from './transaction.model';
import { BudgetProgress } from './budget.model';

export interface MonthlySummary {
  month: number;
  year: number;
  totalIncome: number;
  totalExpenses: number;
  netSavings: number;
}

export interface CategoryBreakdown {
  categoryName: string;
  categoryIcon: string;
  totalAmount: number;
  percentage: number;
  transactionCount: number;
}

export interface DashboardData {
  totalBalance: number;
  monthlyIncome: number;
  monthlyExpenses: number;
  savingsRate: number;
  recentTransactions: Transaction[];
  categoryBreakdown: CategoryBreakdown[];
  monthlyTrend: MonthlySummary[];
  budgetProgress: BudgetProgress[];
}
