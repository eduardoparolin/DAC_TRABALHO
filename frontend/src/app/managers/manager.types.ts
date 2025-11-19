import {LoginResponse} from '../login/login.types';
import {GetClientsResponse} from '../clients/client.types';

export type GetManagersResponse = LoginResponse & {
  clientes: GetClientsResponse[]
}

export type GetManagersResponse2 = {
  cpf: string;
  email: string;
  phone?: string;
  id: number;
  name: string;
}

export type GetManagersAdminDashboardResponse = {
  gerente: GetManagersResponse;
  clientes: GetClientsResponse[];
  saldo_positivo: number;
  saldo_negativo: number;
}
