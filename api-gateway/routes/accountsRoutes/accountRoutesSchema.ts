import { z } from "zod";

export const ContaParamSchema = z.object({
  conta: z.number().int(),
});

export const DepositarSacarSchemaInput = z.object({
  valor: z
    .number()
    .positive("Valor deve ser um número positivo")
    .min(0.01, "Valor deve ser maior que zero"),
});

export const TransferirSchema = z.object({
  destino: z.number().int(),
  valor: z
    .number()
    .positive("Valor deve ser um número positivo")
    .min(0.01, "Valor deve ser maior que zero"),
});
