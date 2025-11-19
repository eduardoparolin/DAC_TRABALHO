import { inject, Injectable, signal } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import {GetManagersResponse, GetManagersResponse2} from './manager.types';
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
      this.http.get<GetManagersResponse2[]>(`${environment.baseUrl}/gerentes`)
    );
    const managers = managersResponse.map((m) => Manager.fromManagerJson(m));
    managers.sort((a, b) => a.name.localeCompare(b.name));
    this.managers.set(managers);
    return managers;
  }

  async removeManager(cpf: string) {
    const managersResponse = await lastValueFrom(
      this.http.delete(`${environment.baseUrl}/gerentes/${cpf}`)
    );
    await this.getAllManagers();
  }
}
