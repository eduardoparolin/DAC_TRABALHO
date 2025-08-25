import { z } from "zod";

export const getCustomersSchemaInput = z.object({
  filtro: z.enum([
    "para_aprovar",
    "adm_relatorio_clientes",
    "melhores_clientes",
  ]),
});

export const createCustomerSchemaInput = z.object({
  cpf: z.string().regex(/^\d{11}$/, "CPF deve ter exatamente 11 dígitos"),
  email: z.email("Email inválido").min(1, "Email é obrigatório"),
  nome: z
    .string()
    .min(2, "Nome deve ter pelo menos 2 caracteres")
    .max(100, "Nome muito longo"),
  telefone: z
    .string()
    .min(10, "Telefone deve ter pelo menos 10 caracteres")
    .max(15, "Telefone muito longo"),
  salario: z
    .number()
    .positive("Salário deve ser um valor positivo")
    .min(0.01, "Salário deve ser maior que zero"),
  endereco: z
    .string()
    .min(5, "Endereço deve ter pelo menos 5 caracteres")
    .max(200, "Endereço muito longo"),
  CEP: z.string().regex(/^\d{8}$/, "CEP deve ter exatamente 8 dígitos"),
  cidade: z
    .string()
    .min(2, "Cidade deve ter pelo menos 2 caracteres")
    .max(50, "Cidade muito longa"),
  estado: z
    .string()
    .length(2, "Estado deve ter exatamente 2 caracteres")
    .regex(/^[A-Z]{2}$/, "Estado deve conter apenas letras maiúsculas"),
});

export const getCustomerByCPFSchemaInput = z.object({
  cpf: z.string().regex(/^\d{11}$/, "CPF deve ter exatamente 11 dígitos"),
});

export const UpdateCustomerByCPFSchemaInput = z.object({
  cpf: z.string().regex(/^\d{11}$/, "CPF deve ter exatamente 11 dígitos"),
});

export const aproveCustomerByCPFSchemaInput = z.object({
  cpf: z.string().regex(/^\d{11}$/, "CPF deve ter exatamente 11 dígitos"),
});

export const declineCustomerByCPFSchemaInput = z.object({
  cpf: z.string().regex(/^\d{11}$/, "CPF deve ter exatamente 11 dígitos"),
});
