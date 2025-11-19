import { Client, ClientJson } from '../clients/client.model';
import { User, UserType, UserJson } from '../session/user.model';
import {GetManagersResponse} from './manager.types';

export interface ManagerJson extends UserJson {
  clientes?: ClientJson[];
  telefone?: string;
}

export class Manager extends User {
  clientList: Client[] = [];

  constructor(
    id: number,
    name: string,
    email: string,
    phone: string,
    cpf: string,
    type: UserType,
    clientList: Client[],
  ) {
    super(id, name, email, phone, cpf, type);
    this.clientList = clientList;
    this.phone = phone;
  }

  static override fromJson(json: GetManagersResponse): Manager {
    const clients =
      json.clientes?.map((clientJson) =>
        Client.fromJson(clientJson)
      ) ?? [];
    const id = typeof json.usuario.id === 'string' ? parseInt(json.usuario.id, 10) : json.usuario.id;
    const tipo = typeof json.tipo === 'string' ? UserType[json.tipo as keyof typeof UserType] : json.tipo;
    return new Manager(
      id,
      json.usuario.name,
      json.usuario.email,
      json.usuario.phone ?? '',
      json.usuario.cpf,
      tipo,
      clients,
    );
  }

  get totalBalance(): number {
    return this.clientList.reduce((total, client) => total + client.balance, 0);
  }
}
