enum UserType {
  ADMIN = 'ADMIN',
  CLIENT = 'CLIENT',
  MANAGER = 'MANAGER'
}

interface UserJson {
  id: number;
  nome: string;
  email: string;
  cpf: string;
  tipo: UserType;
}

class User {
  id: number;
  name: string;
  email: string;
  cpf: string;
  type: UserType;

  constructor(id: number, name: string, email: string, cpf: string, type: UserType) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.cpf = cpf;
    this.type = type;
  }

  static fromJson(json: UserJson): User {
    return new User(json.id, json.nome, json.email, json.cpf, json.tipo);
  }

  isAdmin(): boolean {
    return this.type === UserType.ADMIN;
  }

  isClient(): boolean {
    return this.type === UserType.CLIENT;
  }

  isManager(): boolean {
    return this.type === UserType.MANAGER;
  }
}

export { User, UserType, UserJson }
