export interface Budget {
  id: number;
  name: string;
  limitAmount: number;
  categoryId: number;
  categoryName: string;
  month: number;
  year: number;
}

export interface BudgetProgress extends Budget {
  spentAmount: number;
  remainingAmount: number;
  percentUsed: number;
}
