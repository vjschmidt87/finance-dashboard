export type AccountType = 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD' | 'INVESTMENT' | 'CASH';

export interface Account {
  id: number;
  name: string;
  type: AccountType;
  balance: number;
  currency: string;
}
