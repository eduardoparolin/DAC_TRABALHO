import {effect, inject, Injectable, signal} from '@angular/core';
import {User} from './user.model';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  user = signal<User | null>(null);
  router = inject(Router);
  constructor() {
    effect(() => {
      if (this.user() != null) {
        if (this.user()?.isAdmin()) {
          this.router.navigate(['/dashboard-admin']);
        } else {
          this.router.navigate(['/clientes']);
        }
      } else {
        this.router.navigate(['/login']);
      }
    });
    const localStorageUser = localStorage.getItem('user');
    if (localStorageUser) {
      this.user.set(User.fromJson(JSON.parse(localStorageUser)));
    }
  }

  parseLoginResponse(response: Map<string, string | boolean | number>): User {
    localStorage.setItem('user', JSON.stringify(response));
    const user = User.fromJson(response);
    this.user.set(user);
    return user;
  }

  logout() {
    localStorage.removeItem('user');
    this.user.set(null);
  }
}
