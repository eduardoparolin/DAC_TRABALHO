import {inject, Injectable, signal} from '@angular/core';
import {LoginResponse} from './login.types';
import {lastValueFrom} from 'rxjs';
import {HttpClientMockService} from '../utils/http-client-mock.service';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {SessionService} from '../session/session.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  // http = inject(HttpClient);
  http = inject(HttpClientMockService);
  session = inject(SessionService);
  loading = signal(false);
  constructor() { }

  async login(email: string, password: string) {
    this.loading.set(true);
    await lastValueFrom(this.http.post<LoginResponse>(`${environment.baseUrl}/login`, {email, senha: password}))
      .then(async (response) => {
        this.loading.set(false);
        this.session.parseLoginResponse(response);
      })
      .catch((error) => {
        this.loading.set(false);
        console.error('Erro ao fazer login', error);
      })

  }

  logout() {
    return lastValueFrom(this.http.post(`${environment.baseUrl}/logout`, {}));
  }

  signup() {

  }
}
