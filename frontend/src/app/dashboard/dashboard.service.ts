import {inject, Injectable, signal} from '@angular/core';
import {AdminDashboardManager} from './admin-dashboard-manager.model';
import {HttpClientMockService} from '../utils/http-client-mock.service';
import {lastValueFrom} from 'rxjs';
import {GetManagersAdminDashboardResponse, GetManagersResponse} from '../managers/manager.types';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  dashboardData = signal<AdminDashboardManager[]>([]);
  http = inject(HttpClientMockService);

  constructor() { }

  async getAllDashboardManagers() {
    const managersResponse = await lastValueFrom(this.http.get<GetManagersAdminDashboardResponse[]>(`${environment.baseUrl}/gerentes?filtro=dashboard`))
    const managers = managersResponse.map(manager => AdminDashboardManager.fromJson(manager));
    console.log(managers);
    this.dashboardData.set(managers);
    return managers;
  }
}
