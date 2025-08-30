import {User, UserType} from '../session/user.model';

export class Client extends User {
  balance: number;
  limit: number;
  wage: number;

  constructor(id: number, name: string, email: string, cpf: string, type: UserType, balance: number, limit: number, wage: number) {
    super(id, name, email, cpf, type);
    this.balance = balance;
    this.limit = limit;
    this.wage = wage;
  }

  static override fromJson(json: any): Client {
    return new Client(json.id, json['nome'], json.email, json.cpf, json.type, json['saldo'], json['limite'], json['salario']);
  }
}
