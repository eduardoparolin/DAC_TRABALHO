export type AccountInfoResponse = {
  cliente: string;
  numero: string;
  saldo: number;
  limite: number;
  gerente: string;
  criacao: string;
  status: string;
};

export type ManagerInfoResponse = {
  id: number;
  cpf: string;
  name: string;
  email: string;
};

export type ClientReportResponse = {
  id: number;
  cpf: string;
  name: string;
  email: string;
  phone?: string;
  salary: number;
  salario: number;
  contaId: string;
  accountId?: number;
  status?: string;
  managerId?: number;
  street?: string;
  complement?: string;
  zipCode?: string;
  city?: string;
  state?: string;
  conta?: AccountInfoResponse | null;
  gerente?: ManagerInfoResponse | null;
};

export type ClientDetailsResponse = {
  cpf: string;
  nome: string;
  telefone: string;
  email: string;
  endereco: string;
  cidade: string;
  estado: string;
  salario: number;
  conta: string | null;
  saldo: string | number | null;
  limite: number | null;
  gerente: string | null;
  gerente_nome: string | null;
  gerente_email: string | null;
};
