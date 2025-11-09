import {inject, Injectable, signal} from '@angular/core';
import {LoginResponse} from './login.types';
import {lastValueFrom} from 'rxjs';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {SessionService} from '../session/session.service';
import {ErrorHandlerService} from '../utils/error-handler.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  http = inject(HttpClient);
  session = inject(SessionService);
  errorHandler = inject(ErrorHandlerService);
  loading = signal(false);
  constructor() { }

  async login(email: string, password: string) {
    this.loading.set(true);
    await lastValueFrom(this.http.post<LoginResponse>(`${environment.baseUrl}/auth/login`, {email, password}))
      .then(async (response) => {
        this.loading.set(false);
        this.session.parseLoginResponse(response);
        this.errorHandler.handleSuccess('Login realizado com sucesso!');
      })
      .catch((error) => {
        this.loading.set(false);
        this.errorHandler.handleError(error, { showError: true });
      })

  }

  logout() {
    return lastValueFrom(this.http.post(`${environment.baseUrl}/logout`, {}));
  }

  signup() {

  }
}
