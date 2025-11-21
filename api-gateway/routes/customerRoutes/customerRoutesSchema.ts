import { z } from "zod";

export const getCustomersSchemaInput = z.object({
  filtro: z.enum([
    "para_aprovar",
    "adm_relatorio_clientes",
    "melhores_clientes",
  ]).optional(),
  managerId: z.string().optional(),
  cpf: z.string().optional(),
  name: z.string().optional(),
});

export const createCustomerSchemaInput = z.object({
  cpf: z
    .string()
    .min(11, "CPF deve ter 11 dígitos")
    .max(11, "CPF deve ter 11 dígitos"),
  nome: z.string().min(1, "Nome é obrigatório"),
  email: z.string().email("Email inválido"),
  telefone: z.string().min(1, "Telefone é obrigatório"),
  salario: z.number().min(0, "Salário deve ser positivo"),
  endereco: z.string().min(1, "Endereço é obrigatório"),
  numero: z.string().optional(),
  complemento: z.string().optional(),
  CEP: z
    .string()
    .min(8, "CEP deve ter 8 dígitos")
    .max(8, "CEP deve ter 8 dígitos"),
  cidade: z.string().min(1, "Cidade é obrigatória"),
  estado: z
    .string()
    .min(2, "Estado deve ter 2 letras")
    .max(2, "Estado deve ter 2 letras"),
});

export const updateCustomerSchemaInput = createCustomerSchemaInput;

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

export const declineCustomerBodySchema = z.object({
  motivo: z.string().min(1, "Motivo da rejeição é obrigatório"),
});
