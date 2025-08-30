import { Routes } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ClientsComponent} from './clients/clients.component';
import {ManagersComponent} from './managers/managers.component';
import {AccountsComponent} from './accounts/accounts.component';

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
    path: 'gestores',
    component: ManagersComponent
  },
  {
    path: 'contas',
    component: AccountsComponent
  }
];
