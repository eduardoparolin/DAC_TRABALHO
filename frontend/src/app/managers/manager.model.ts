import { Client, ClientJson } from '../clients/client.model';
import { User, UserType, UserJson } from '../session/user.model';

export interface ManagerJson extends UserJson {
  clientes?: ClientJson[];
  telefone?: string;
}

export class Manager extends User {
  clientList: Client[] = [];
  phone: string;

  constructor(
    id: number,
    name: string,
    email: string,
    cpf: string,
    type: UserType,
    clientList: Client[],
    phone: string
  ) {
    super(id, name, email, cpf, type);
    this.clientList = clientList;
    this.phone = phone;
  }

  static override fromJson(json: ManagerJson): Manager {
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
      json.usuario.cpf,
      tipo,
      clients,
      json.telefone ?? ''
    );
  }

  get totalBalance(): number {
    return this.clientList.reduce((total, client) => total + client.balance, 0);
  }
}
