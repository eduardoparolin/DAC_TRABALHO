import { inject, Injectable, signal } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { GetManagersResponse } from './manager.types';
import { Manager } from './manager.model';

@Injectable({
  providedIn: 'root',
})
export class ManagersService {
  http = inject(HttpClient);
  managers = signal<Manager[]>([]);

  constructor() {}

  async getAllManagers() {
    const managersResponse = await lastValueFrom(
      this.http.get<GetManagersResponse[]>(`${environment.baseUrl}/gerentes`)
    );
    const managers = managersResponse.map((m) => Manager.fromJson(m));
    this.managers.set(managers);
    return managers;
  }
}
