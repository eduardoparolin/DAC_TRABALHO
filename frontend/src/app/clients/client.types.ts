import {LoginResponse} from '../login/login.types';

export type GetClientsResponse = Omit<LoginResponse, 'type'> & {
  "endereco": string;
  "cidade": string;
  "estado": string;
  "numero": string;
  "saldo": number;
  "limite": number;
  "salario": number;
  "gerente": string;
  "criacao": string
}
