import { Hono } from "hono";
import { z } from "zod";
import { zValidator } from "@hono/zod-validator";

export const authRoutes = new Hono();

const authServiceUrl = process.env.AUTH_SERVICE_URL;
const orchestratorServiceUrl =
  process.env.ORCHESTRATOR_SERVICE_URL || "http://orchestrator-service:8085";

const loginSchema = z.object({
  email: z.email("Email inválido"),
  password: z.string().min(1, "Senha é obrigatória"),
});

const signupSchema = z.object({
  cpf: z
    .string()
    .min(11, "CPF deve ter 11 dígitos")
    .max(11, "CPF deve ter 11 dígitos"),
  name: z.string().min(1, "Nome é obrigatório"),
  email: z.string().email("Email inválido"),
  phone: z.string().min(1, "Telefone é obrigatório"),
  salary: z.number().min(0, "Salário deve ser positivo"),
  street: z.string().min(1, "Rua é obrigatória"),
  number: z.string().min(1, "Número é obrigatório"),
  complement: z.string().optional(),
  zipCode: z
    .string()
    .min(8, "CEP deve ter 8 dígitos")
    .max(8, "CEP deve ter 8 dígitos"),
  city: z.string().min(1, "Cidade é obrigatória"),
  state: z
    .string()
    .min(2, "Estado deve ter 2 letras")
    .max(2, "Estado deve ter 2 letras"),
});

const logoutSchema = z.object({
  cpf: z.string().min(11, "CPF inválido").max(11, "CPF inválido"),
  nome: z.string().min(1, "Nome é obrigatório"),
  email: z.email(),
  tipo: z.string(),
});

authRoutes.post("/login", zValidator("json", loginSchema), async (c) => {
  try {
    const { email, password } = c.req.valid("json");
      const response = await fetch(`${authServiceUrl}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
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

authRoutes.post("/signup", zValidator("json", signupSchema), async (c) => {
  try {
    const clientData = c.req.valid("json");

    const response = await fetch(
      `${orchestratorServiceUrl}/api/saga/client/create`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          action: "CREATE_CLIENT",
          data: clientData,
        }),
      }
    );

    if (!response.ok) {
      const errorData = await response.json();
      return c.json(
        {
          error: "Falha ao iniciar cadastro",
          details: errorData,
        },
        response.status as any
      );
    }

    const data = await response.json();

    return c.json(
      {
        message: "Cadastro iniciado com sucesso",
        sagaId: data.sagaId,
        status: data.status,
      },
      202
    );
  } catch (error) {
    console.error("Erro ao disparar saga de cadastro:", error);
    return c.json({ error: "Serviço de orquestração indisponível" }, 503);
  }
});
