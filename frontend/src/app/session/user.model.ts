enum UserType {
  ADMIN = 'ADMIN',
  CLIENT = 'CLIENT',
  MANAGER = 'MANAGER'
}

interface UserJson {
  id: number | string;
  nome: string;
  email: string;
  cpf: string;
  tipo: UserType | string;
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
    const id = typeof json.id === 'string' ? parseInt(json.id, 10) : json.id;
    const tipo = typeof json.tipo === 'string' ? UserType[json.tipo as keyof typeof UserType] : json.tipo;
    return new User(id, json.nome, json.email, json.cpf, tipo);
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

export { User, UserType };
export type { UserJson };
