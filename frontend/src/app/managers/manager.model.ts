import { Client } from '../clients/client.model';
import { User, UserType } from '../session/user.model';

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

  static override fromJson(json: any): Manager {
    const clients =
      (json['clientes'] as any[])?.map((clientJson) =>
        Client.fromJson(clientJson)
      ) ?? [];
    return new Manager(
      json['id'],
      json['nome'],
      json['email'],
      json['cpf'],
      json['tipo'],
      clients,
      json['phone']
    );
  }

  get totalBalance(): number {
    return this.clientList.reduce((total, client) => total + client.balance, 0);
  }
}
