import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { User, AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = environment.apiUrl;

  private isLoggedInSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  private currentUserSubject = new BehaviorSubject<User | null>(this.loadUserFromStorage());

  isLoggedIn$ = this.isLoggedInSubject.asObservable();
  currentUser$ = this.currentUserSubject.asObservable();

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, { email, password } as LoginRequest).pipe(
      tap((response) => {
        localStorage.setItem('auth_token', response.token);
        const user: User = { email: response.email, firstName: response.firstName, lastName: '' };
        localStorage.setItem('auth_user', JSON.stringify(user));
        this.isLoggedInSubject.next(true);
        this.currentUserSubject.next(user);
      })
    );
  }

  register(email: string, password: string, firstName: string, lastName: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, {
      email, password, firstName, lastName
    } as RegisterRequest).pipe(
      tap((response) => {
        localStorage.setItem('auth_token', response.token);
        const user: User = { email: response.email, firstName: response.firstName, lastName };
        localStorage.setItem('auth_user', JSON.stringify(user));
        this.isLoggedInSubject.next(true);
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    this.isLoggedInSubject.next(false);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('auth_token');
  }

  private loadUserFromStorage(): User | null {
    const userJson = localStorage.getItem('auth_user');
    if (userJson) {
      try {
        return JSON.parse(userJson);
      } catch {
        return null;
      }
    }
    return null;
  }
}
