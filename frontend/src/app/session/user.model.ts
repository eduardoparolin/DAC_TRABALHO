enum UserType {
  ADMINISTRADOR = 'ADMINISTRADOR',
  CLIENTE = 'CLIENTE',
  GERENTE = 'GERENTE',
}

interface UserJson {
  usuario: {
    id: number;
    name: string;
    cpf: string;
    phone: string;
    email: string;
    zipCode?: string;
    state?: string;
    street?: string;
    city?: string;
    complement?: string;
  };
  tipo: UserType | string;
}

class User {
  id: number;
  name: string;
  email: string;
  phone: string;
  cpf: string;
  type: UserType;
  zipCode?: string;
  state?: string;
  street?: string;
  city?: string;
  complement?: string;

  constructor(
    id: number,
    name: string,
    email: string,
    phone: string,
    cpf: string,
    type: UserType
  ) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.cpf = cpf;
    this.type = type;
  }

  static fromJson(json: UserJson): User {
    const id =
      typeof json.usuario.id === 'string'
        ? parseInt(json.usuario.id, 10)
        : json.usuario.id;
    const tipo =
      typeof json.tipo === 'string'
        ? UserType[json.tipo as keyof typeof UserType]
        : json.tipo;
    const myUser = new User(
      id,
      json.usuario.name,
      json.usuario.email,
      json.usuario.phone,
      json.usuario.cpf,
      tipo
    );
    myUser.zipCode = json.usuario.zipCode;
    myUser.state = json.usuario.state;
    myUser.street = json.usuario.street;
    myUser.city = json.usuario.city;
    myUser.complement = json.usuario.complement;
    return myUser;
  }

  isAdmin(): boolean {
    return this.type === UserType.ADMINISTRADOR;
  }

  isClient(): boolean {
    return this.type === UserType.CLIENTE;
  }

  isManager(): boolean {
    return this.type === UserType.GERENTE;
  }
}

export { User, UserType };
export type { UserJson };
