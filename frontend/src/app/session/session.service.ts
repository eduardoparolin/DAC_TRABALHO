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
        this.router.navigate(['/clientes']);
      } else {
        this.router.navigate(['/login']);
      }
    })
  }

  parseLoginResponse(response: Map<string, string | boolean | number>): User {
    const user = User.fromJson(response);
    this.user.set(user);
    return user;
  }
}
