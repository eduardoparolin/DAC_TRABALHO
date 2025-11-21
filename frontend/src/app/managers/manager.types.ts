import {LoginResponse} from '../login/login.types';
import {GetClientsResponse} from '../clients/client.types';
import { UserType } from '../session/user.model';

export type GetManagersResponse = LoginResponse & {
  clientes: GetClientsResponse[]
}

export type GetManagersResponse2 = {
  cpf: string;
  email: string;
  phone?: string;
  id: number;
  name: string;
  type: UserType;
}

export type GetManagersAdminDashboardResponse = {
  gerente: GetManagersResponse;
  clientes: GetClientsResponse[];
  saldo_positivo: number;
  saldo_negativo: number;
}

export type updateManagerRequest = {
  nome: string;
  email: string;
  senha: string;
  tipo: 'GERENTE';
}

export type insertManagerRequest = updateManagerRequest & {
  cpf: string;
}
