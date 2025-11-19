import {ClientJson} from '../clients/client.model';
import {User, UserJson, UserType} from '../session/user.model';
import {GetManagersResponse2} from './manager.types';

export interface ManagerJson extends UserJson {
  clientes?: ClientJson[];
  telefone?: string;
}

export class Manager extends User {
  constructor(
    id: number,
    name: string,
    email: string,
    cpf: string,
    type: UserType,
    phone?: string,
  ) {
    super(id, name, email, phone ?? '', cpf, type);
  }

  static fromManagerJson(json: GetManagersResponse2): Manager {
    // const clients =
    //   json.clientes?.map((clientJson) =>
    //     Client.fromJson(clientJson)
    //   ) ?? [];
    const id = json.id;
    // const tipo = typeof json.tipo === 'string' ? UserType[json.tipo as keyof typeof UserType] : json.tipo;
    return new Manager(
      id,
      json.name,
      json.email,
      json.cpf,
      UserType.GERENTE,
      json.phone,
    );
  }
}
