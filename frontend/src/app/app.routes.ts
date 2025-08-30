import { Routes } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ClientsComponent} from './clients/clients.component';
import {ManagersComponent} from './managers/managers.component';
import {AccountsComponent} from './accounts/accounts.component';
import {DashboardComponent} from './dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'clientes',
    component: ClientsComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'gerentes',
    component: ManagersComponent
  },
  {
    path: 'contas',
    component: AccountsComponent
  }
];
