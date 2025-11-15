export type ClientResponse = {
  "id": number;
  "cpf": string;
  "nome": string;
  "email": string;
  "tipo": string;
  "saldo": number;
  "limite": number;
  "salario": number;
  "endereco": string;
  "cidade"?: string;
  "estado"?: string;
  "telefone"?: string;
  "numero_conta"?: string;
  "gerente"?: string;
  "gerente_nome"?: string;
}
