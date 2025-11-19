import {LoginResponse} from '../login/login.types';
import {GetClientsResponse} from '../clients/client.types';

export type GetManagersResponse = LoginResponse & {
  clientes: GetClientsResponse[]
}

export type GetManagersAdminDashboardResponse = {
  gerente: GetManagersResponse;
  clientes: GetClientsResponse[];
  saldo_positivo: number;
  saldo_negativo: number;
}
