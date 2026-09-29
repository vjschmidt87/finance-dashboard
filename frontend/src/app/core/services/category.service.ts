import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Category } from '../models/category.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/categories`;

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
  }

  getIncomeCategories(): Observable<Category[]> {
    return this.getCategories().pipe(
      map(categories => categories.filter(c => c.type === 'INCOME'))
    );
  }

  getExpenseCategories(): Observable<Category[]> {
    return this.getCategories().pipe(
      map(categories => categories.filter(c => c.type === 'EXPENSE'))
    );
  }

  createCategory(category: Partial<Category>): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category);
  }
}
