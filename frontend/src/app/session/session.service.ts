import { effect, inject, Injectable, signal } from '@angular/core';
import { User, UserJson } from './user.model';
import { Router } from '@angular/router';

export interface UserWithBalance extends User {
  balance: number;
}

@Injectable({
  providedIn: 'root',
})
export class SessionService {
  user = signal<User | null>(null);
  meClient = signal<UserWithBalance | null>(null);
  router = inject(Router);
  constructor() {
    effect(() => {
      if (this.user() != null) {
        if (this.user()?.isAdmin()) {
          this.router.navigate(['/dashboard-admin']);
        } else if (this.user()?.isManager()) {
          this.router.navigate(['/aprovacao-clientes']);
        } else {
          this.router.navigate(['/extrato']);
        }
      } else {
        this.router.navigate(['/login']);
      }
    });
    const localStorageUser = localStorage.getItem('user');
    if (localStorageUser) {
      const parsedUser = JSON.parse(localStorageUser) as UserJson;
      const user = User.fromJson(parsedUser);
      this.user.set(user);
      this.meClient.set(Object.assign(user, { balance: 1000 }));
    }
  }

  parseLoginResponse(response: UserJson): User {
    localStorage.setItem('user', JSON.stringify(response));
    const user = User.fromJson(response);
    this.user.set(user);
    this.meClient.set(Object.assign(user, { balance: Math.random() * 1000 }));
    return user;
  }

  logout() {
    localStorage.removeItem('user');
    this.user.set(null);
    this.meClient.set(null);
  }

  getUser(): User | null {
    return this.user();
  }

  isAuthenticated(): boolean {
    return this.user() !== null;
  }
}
