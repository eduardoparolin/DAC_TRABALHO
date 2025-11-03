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
import {AccountStatementComponent} from './account-statement/account-statement.component';
import {SignupComponent} from './signup/signup.component';
import {authGuard} from './guards/auth.guard';
import {adminGuard, adminOrManagerGuard, clientGuard, managerGuard} from './guards/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'cadastro',
    component: SignupComponent
  },
  {
    path: 'clientes',
    component: ClientsComponent,
    canActivate: [authGuard, adminOrManagerGuard]
  },
  {
    path: 'dashboard-admin',
    component: DashboardComponent,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'aprovacao-clientes',
    component: ClientApprovalComponent,
    canActivate: [authGuard, managerGuard]
  },
  {
    path: 'busca-clientes',
    component: ClientSearchComponent,
    canActivate: [authGuard, adminOrManagerGuard]
  },
  {
    path: 'top-3-clientes',
    component: ClientLeaderboardComponent,
    canActivate: [authGuard, adminOrManagerGuard]
  },
  {
    path: 'gerentes',
    component: ManagersComponent,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'depositar',
    component: DepositComponent,
    canActivate: [authGuard, clientGuard]
  },
  {
    path: 'sacar',
    component: WithdrawComponent,
    canActivate: [authGuard, clientGuard]
  },
  {
    path: 'transferir',
    component: TransferComponent,
    canActivate: [authGuard, clientGuard]
  },
  {
    path: 'contas',
    component: AccountsComponent,
    canActivate: [authGuard, adminOrManagerGuard]
  },
  {
    path: 'extrato',
    component: AccountStatementComponent,
    canActivate: [authGuard, clientGuard]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  }
];
