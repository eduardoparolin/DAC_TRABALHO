import { Routes } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ClientsComponent} from './clients/clients.component';
import {ManagersComponent} from './managers/managers.component';
import {AccountsComponent} from './accounts/accounts.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {ClientApprovalComponent} from './client-approval/client-approval.component';
import {ClientSearchComponent} from './client-search/client-search.component';

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
    path: 'dashboard-admin',
    component: DashboardComponent
  },
  {
    path: 'aprovacao-clientes',
    component: ClientApprovalComponent
  },
  {
    path: 'busca-clientes',
    component: ClientSearchComponent
  },
  {
    path: 'top-3-clientes',
    component: ClientApprovalComponent
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
