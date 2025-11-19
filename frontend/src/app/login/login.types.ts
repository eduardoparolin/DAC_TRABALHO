import { UserType } from "../session/user.model";

export type LoginResponse = {
  accessToken: string;
  access_token?: string;
  tokenType: string;
  token_type?: string;
  usuario: {
    id: number;
    name: string;
    phone: string;
    cpf: string;
    email: string;
  };
  tipo: UserType | string;
};
