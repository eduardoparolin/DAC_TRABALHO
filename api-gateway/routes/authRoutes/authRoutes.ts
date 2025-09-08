import { Hono } from "hono";
import { z } from "zod";
import { zValidator } from "@hono/zod-validator";

export const authRoutes = new Hono();

const authServiceUrl = process.env.AUTH_SERVICE_URL;

const loginSchema = z.object({
  email: z.email("Email inválido"),
  senha: z.string().min(1, "Senha é obrigatória"),
});

const logoutSchema = z.object({
  cpf: z.string().min(11, "CPF inválido").max(11, "CPF inválido"),
  nome: z.string().min(1, "Nome é obrigatório"),
  email: z.email(),
  tipo: z.string(),
});

authRoutes.post("/login", zValidator("json", loginSchema), async (c) => {
  try {
    const { email, senha } = c.req.valid("json");

    const response = await fetch(`${authServiceUrl}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, senha }),
    });

    const data = await response.text();

    return new Response(data, {
      status: response.status,
      headers: {
        "Content-Type": "application/json",
      },
    });
  } catch (error) {
    return c.json({ error: "Serviço de autenticação indisponível" }, 503);
  }
});

authRoutes.post("/logout", zValidator("json", logoutSchema), async (c) => {
  const { cpf, nome, email, tipo } = c.req.valid("json");
  try {
    const response = await fetch(`${authServiceUrl}/auth/logout`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ cpf, nome, email, tipo }),
    });

    const data = await response.text();

    return new Response(data, {
      status: response.status,
      headers: {
        "Content-Type": "application/json",
      },
    });
  } catch (error) {
    return c.json({ error: "Serviço de autenticação indisponível" }, 503);
  }
});
