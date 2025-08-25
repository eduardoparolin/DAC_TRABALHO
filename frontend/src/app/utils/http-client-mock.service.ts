import { Injectable } from '@angular/core';
import {delay, Observable, of} from 'rxjs';
import loginMockResponse from './mocks/login.mock.json';
import clientesGetMockResponse from './mocks/clients.mock.json';

@Injectable({
  providedIn: 'root'
})
export class HttpClientMockService {

  constructor() { }

  post<T>(url: string, body: any): Observable<any> {
    if (url.includes('/login')) {
      return of(loginMockResponse).pipe(delay(500)); // 500ms delay
    }
    if (url.includes('/logout')) {
      return of({}).pipe(delay(500));
    }
    return of().pipe(delay(500));
  }

  get<T>(url: string): Observable<any> {
    if (url.includes('/clientes')) {
      return of(clientesGetMockResponse).pipe(delay(500));
    }
    return of().pipe(delay(500));
  }
}
