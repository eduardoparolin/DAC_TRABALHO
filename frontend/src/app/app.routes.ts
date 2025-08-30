import { Routes } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ClientsComponent} from './clients/clients.component';
import {ManagersComponent} from './managers/managers.component';
import {AccountsComponent} from './accounts/accounts.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {ClientApprovalComponent} from './client-approval/client-approval.component';
import {ClientSearchComponent} from './client-search/client-search.component';
import {ClientLeaderboardComponent} from './client-leaderboard/client-leaderboard.component';
import {DepositComponent} from './deposit/deposit.component';
import {WithdrawComponent} from './withdraw/withdraw.component';
import {TransferComponent} from './transfer/transfer.component';

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
    component: ClientLeaderboardComponent
  },
  {
    path: 'gerentes',
    component: ManagersComponent
  },
  {
    path: 'depositar',
    component: DepositComponent
  },
  {
    path: 'sacar',
    component: WithdrawComponent
  },
  {
    path: 'transferir',
    component: TransferComponent
  },
  {
    path: 'contas',
    component: AccountsComponent
  }
];
