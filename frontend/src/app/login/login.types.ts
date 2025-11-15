import { UserType } from "../session/user.model";

export type LoginResponse = {
  usuario: {
    id: number,
    name: string,
    cpf: string,
    email: string,
  }
  tipo: UserType | string;
}
