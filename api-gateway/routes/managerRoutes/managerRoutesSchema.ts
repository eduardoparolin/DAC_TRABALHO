import { z } from "zod";

export const CPFParamSchema = z.object({
  cpf: z.string().regex(/^\d{11}$/, "CPF deve ter exatamente 11 dígitos"),
});

export const getManagersSchema = z.object({
  filtro: z.enum(["dashboard"]).optional(),
});

export const createManagerSchemaInput = z.object({
  cpf: z.string().regex(/^\d{11}$/, "CPF deve ter exatamente 11 dígitos"),
  nome: z.string(),
  email: z.email("Email inválido").min(1, "Email é obrigatório"),
  tipo: z.string(),
  senha: z.string(),
});

export const updateManagerSchemaInput = z.object({
  nome: z.string().optional(),
  email: z.email("Email inválido").min(1, "Email é obrigatório").optional(),
  senha: z.string().optional(),
  tipo: z.string().optional(),
});
