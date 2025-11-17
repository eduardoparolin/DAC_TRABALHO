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
    try {
      const response = await lastValueFrom(
        this.http.post<LoginResponse>(`${environment.baseUrl}/login`, {
          login: email,
          senha: password
        })
      );
      this.session.parseLoginResponse(response);
      this.errorHandler.handleSuccess('Login realizado com sucesso!');
    } catch (error) {
      this.errorHandler.handleError(error as Error, { showError: true });
    } finally {
      this.loading.set(false);
    }

  }

  logout() {
    return lastValueFrom(this.http.post(`${environment.baseUrl}/logout`, {}));
  }

  signup() {

  }
}
