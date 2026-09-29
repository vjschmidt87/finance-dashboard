import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardData } from '../models/dashboard.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/dashboard`;

  getDashboard(month?: number, year?: number): Observable<DashboardData> {
    let params = new HttpParams();
    if (month !== undefined) {
      params = params.set('month', month.toString());
    }
    if (year !== undefined) {
      params = params.set('year', year.toString());
    }
    return this.http.get<DashboardData>(this.apiUrl, { params });
  }
}
