import { User, UserType } from '../session/user.model';

export class Client extends User {
  balance: number;
  limit: number;
  wage: number;
  city: string;
  state: string;
  phone: string;
  accountNumber: string;
  cpfManager: string;
  nameManager: string;

  constructor(
    id: number,
    name: string,
    email: string,
    cpf: string,
    type: UserType,
    balance: number,
    limit: number,
    wage: number,
    city?: string,
    state?: string,
    phone?: string,
    accountNumber?: string,
    cpfManager?: string,
    nameManager?: string
  ) {
    super(id, name, email, cpf, type);
    this.balance = balance;
    this.limit = limit;
    this.wage = wage;
    this.city = city || '';
    this.state = state || '';
    this.phone = phone || '';
    this.accountNumber = accountNumber || '';
    this.cpfManager = cpfManager || '';
    this.nameManager = nameManager || '';
  }

  static override fromJson(json: any): Client {
    return new Client(
      json.id,
      json['nome'],
      json.email,
      json.cpf,
      json.type,
      json['saldo'],
      json['limite'],
      json['salario'],
      json['cidade'],
      json['estado'],
      json['telefone'],
      json['numero_conta'],
      json['gerente'],
      json['gerente_nome']
    );
  }
}
