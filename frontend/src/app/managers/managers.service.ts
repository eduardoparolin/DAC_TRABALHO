import {inject, Injectable} from '@angular/core';
import {lastValueFrom} from 'rxjs';
import {environment} from '../../environments/environment';
import {HttpClientMockService} from '../utils/http-client-mock.service';
import {GetManagersResponse} from './manager.types';

@Injectable({
  providedIn: 'root'
})
export class ManagersService {
  // http = inject(HttpClient);
  http = inject(HttpClientMockService);

  constructor() { }

  async getAllManagers() {
    const managers = await lastValueFrom(this.http.get<GetManagersResponse[]>(`${environment.baseUrl}/gerentes`))
    return managers;
  }
}
