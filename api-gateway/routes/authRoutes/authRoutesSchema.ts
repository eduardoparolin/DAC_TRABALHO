import { z } from "zod";

export const loginSchema = z.object({
  email: z.email("Email inválido"),
  senha: z
    .string()
    .min(3, "Senha deve ter pelo menos 3 caracteres")
    .max(50, "Senha muito longa"),
});
