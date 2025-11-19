import { effect, inject, Injectable, signal } from '@angular/core';
import { User, UserJson } from './user.model';
import { Router } from '@angular/router';
import { LoginResponse } from '../login/login.types';
import {ClientAccountService} from '../utils/client-account.service';

export interface UserWithBalance extends User {
  balance: number;
  salary?: number;
}

@Injectable({
  providedIn: 'root',
})
export class SessionService {
  user = signal<User | null>(null);
  meClient = signal<UserWithBalance | null>(null);
  router = inject(Router);
  private readonly storageKey = 'session';

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
    const localStorageUser = localStorage.getItem(this.storageKey);
    if (localStorageUser) {
      try {
        const parsedUser = JSON.parse(localStorageUser) as LoginResponse;
        this.applyLoginResponse(parsedUser);
      } catch (error) {
        console.error('Erro ao carregar sessão do usuário', error);
        localStorage.removeItem(this.storageKey);
      }
    }
  }

  parseLoginResponse(response: LoginResponse): User {
    localStorage.setItem(this.storageKey, JSON.stringify(response));
    this.applyLoginResponse(response);
    return this.user()!;
  }

  private applyLoginResponse(response: LoginResponse) {
    const userJson: UserJson = {
      usuario: response.usuario,
      tipo: response.tipo
    };
    const user = User.fromJson(userJson);
    this.user.set(user);
    this.meClient.set(Object.assign({}, user, { balance: 0 }));
  }

  logout() {
    localStorage.removeItem(this.storageKey);
    this.user.set(null);
    this.meClient.set(null);
  }

  getUser(): User | null {
    return this.user();
  }

  isAuthenticated(): boolean {
    return this.user() !== null;
  }

  getToken(): string | null {
    const stored = localStorage.getItem(this.storageKey);
    if (!stored) {
      return null;
    }

    try {
      const parsed = JSON.parse(stored) as LoginResponse;
      return parsed.accessToken || parsed.access_token || null;
    } catch {
      return null;
    }
  }
}
