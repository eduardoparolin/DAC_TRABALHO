import { Hono } from "hono";
import { z } from "zod";
import { zValidator } from "@hono/zod-validator";
import { authMiddleware } from "../../middleware/auth";

export const authRoutes = new Hono();

const authValidator = (schema: z.ZodSchema) =>
  zValidator("json", schema, (result, c) => {
    if (!result.success) {
      return c.json(
        {
          error: "Dados de autenticação inválidos",
          details: result.error.issues,
        },
        401
      );
    }
  });

const getServiceUrls = () => ({
  authServiceUrl: process.env.AUTH_SERVICE_URL,
  orchestratorServiceUrl: process.env.ORCHESTRATOR_SERVICE_URL,
});

const loginSchema = z.object({
  login: z.string().min(1, "Email é obrigatório"),
  senha: z.string().min(1, "Senha é obrigatória"),
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

authRoutes.post("/login", authValidator(loginSchema), async (c) => {
  try {
    const { login, senha } = c.req.valid("json") as z.infer<typeof loginSchema>;
    const { authServiceUrl } = getServiceUrls();
      const response = await fetch(`${authServiceUrl}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email: login, password: senha }),
    });

    const responseData = await response.json();
      console.log(login, senha, responseData);
      const mappedData = {
      ...responseData,
      access_token: responseData.accessToken,
      token_type: responseData.tokenType,
    };

    return c.json(mappedData, response.status as any);
  } catch (error) {
    return c.json({ error: "Serviço de autenticação indisponível" }, 503);
  }
});

authRoutes.post("/logout", authMiddleware, async (c) => {
  try {
    const { authServiceUrl } = getServiceUrls();
    const jwtPayload = c.get("jwtPayload") as any;

    const email = jwtPayload?.email || jwtPayload?.sub;

    if (!email) {
      return c.json({ error: "Email não encontrado no token" }, 400);
    }

    const response = await fetch(`${authServiceUrl}/auth/logout`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email }),
    });

    const data = await response.text();

    return c.json({ email }, response.status as any);
  } catch (error) {
    return c.json({ error: "Serviço de autenticação indisponível" }, 503);
  }
});

authRoutes.post("/signup", zValidator("json", signupSchema), async (c) => {
  try {
    const clientData = c.req.valid("json");
    const { orchestratorServiceUrl } = getServiceUrls();

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
