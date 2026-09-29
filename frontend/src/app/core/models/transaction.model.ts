export type TransactionType = 'INCOME' | 'EXPENSE' | 'TRANSFER';

export interface Transaction {
  id: number;
  amount: number;
  description: string;
  date: string;
  type: TransactionType;
  accountId: number;
  categoryId: number;
  categoryName: string;
  accountName: string;
}

export interface TransactionFilter {
  dateFrom?: string;
  dateTo?: string;
  accountId?: number;
  categoryId?: number;
  page?: number;
  size?: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
