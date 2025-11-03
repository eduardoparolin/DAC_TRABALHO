import { User, UserType, UserJson } from '../session/user.model';

export interface ClientJson extends UserJson {
  saldo: number;
  limite: number;
  salario: number;
  cidade?: string;
  estado?: string;
  telefone?: string;
  numero_conta?: string;
  gerente?: string;
  gerente_nome?: string;
}

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

  static override fromJson(json: ClientJson): Client {
    return new Client(
      json.id,
      json.nome,
      json.email,
      json.cpf,
      json.tipo,
      json.saldo,
      json.limite,
      json.salario,
      json.cidade,
      json.estado,
      json.telefone,
      json.numero_conta,
      json.gerente,
      json.gerente_nome
    );
  }
}
