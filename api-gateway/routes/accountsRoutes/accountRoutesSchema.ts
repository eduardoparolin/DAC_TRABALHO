import { z } from "zod";

export const movementRequestSchema = z.object({
  valor: z.number().positive("Valor deve ser positivo"),
});

export const transferRequestSchema = z.object({
  valor: z.number().positive("Valor deve ser positivo"),
  destino: z.string().min(1, "Conta de destino é obrigatória"),
});

export const accountNumberParamSchema = z.object({
  numero: z.string().min(1, "Número da conta é obrigatório"),
});

export const managerIdParamSchema = z.object({
  idGerente: z.string().min(1, "ID do gerente é obrigatório"),
});

export const clientIdParamSchema = z.object({
  idCliente: z.string().min(1, "ID do cliente é obrigatório"),
});

export const accountsRequestSchema = z.object({
  accountNumbers: z.array(z.string()).optional(),
});

export const includeTransactionsQuerySchema = z.object({
  incluirTransacoes: z
    .enum(["true", "false"])
    .optional()
    .default("false")
    .transform((val) => val === "true"),
});
