import {inject, Injectable} from '@angular/core';
import {LoginResponse} from './login.types';
import {lastValueFrom} from 'rxjs';
import {HttpClientMockService} from '../utils/http-client-mock.service';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  // http = inject(HttpClient);
  http = inject(HttpClientMockService);
  constructor() { }

  login(email: string, password: string) {
    return lastValueFrom(this.http.post<LoginResponse>(`${environment.baseUrl}/login`, {email, senha: password}));
  }

  logout() {
    return lastValueFrom(this.http.post(`${environment.baseUrl}/logout`, {}));
  }

  signup() {

  }
}
