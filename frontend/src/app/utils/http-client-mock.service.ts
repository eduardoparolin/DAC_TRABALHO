import { Injectable } from '@angular/core';
import {delay, Observable, of, throwError} from 'rxjs';
import loginMockResponse from './mocks/login.mock.json';
import clientesGetMockResponse from './mocks/clients.mock.json';
import managersGetMockResponse from './mocks/managers.mock.json';
import managersAdminDashGetMockResponse from './mocks/managers.mock.json';

@Injectable({
  providedIn: 'root'
})
export class HttpClientMockService {

  constructor() { }

  post<T>(url: string, body: any): Observable<any> {
    if (url.includes('/login')) {
      if (body.email === 'eduardo+admin@nuvex.tech') {
        return of(loginMockResponse.admin).pipe(delay(500));
      } else if (body.email === 'eduardo+manager@nuvex.tech') {
        return of(loginMockResponse.manager).pipe(delay(500));
      } else if (body.email === 'eduardo+client@nuvex.tech') {
        return of(loginMockResponse.client).pipe(delay(500));
      } else {
        return throwError(() => 'Invalid email').pipe(delay(500));
      }
    }
    if (url.includes('/logout')) {
      return of({}).pipe(delay(500));
    }
    return of().pipe(delay(500));
  }

  get<T>(url: string): Observable<T> {
    if (url.includes('/clientes')) {
      return of(clientesGetMockResponse as T).pipe(delay(500));
    }
    if (url.includes('/gerentes')) {
      if (url.includes('filtro=dashboard')) {
        return of(managersAdminDashGetMockResponse as T).pipe(delay(500));
      }
      return of(managersGetMockResponse as T).pipe(delay(500));
    }
    return of().pipe(delay(500));
  }
}
