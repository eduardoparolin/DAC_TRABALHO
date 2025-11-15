import { Hono } from "hono";
import { zValidator } from "@hono/zod-validator";
import {
  aproveCustomerByCPFSchemaInput,
  createCustomerSchemaInput,
  declineCustomerByCPFSchemaInput,
  getCustomerByCPFSchemaInput,
  getCustomersSchemaInput,
} from "./customerRoutesSchema";
import { customersMock } from "./customerRoutes.mock";
import { z } from "zod";

export const customerRoutes = new Hono();
const clientServiceUrl = process.env.CLIENT_SERVICE_URL;
const orchestratorServiceUrl =
  process.env.ORCHESTRATOR_SERVICE_URL || "http://orchestrator-service:8085";

const updateCustomerSchema = z.object({
  cpf: z.string(),
  name: z.string(),
  email: z.string().email(),
  salary: z.number(),
  clientId: z.number(),
});

customerRoutes.get(
  "/",
  zValidator("query", getCustomersSchemaInput),
  async (c) => {
    return c.json(customersMock, 200);
  }
);

customerRoutes.get(
  "/:cpf",
  zValidator("param", getCustomerByCPFSchemaInput),
  async (c) => {
    return c.json(customersMock[0], 200);
  }
);

customerRoutes.post(
  "/",
  zValidator("json", createCustomerSchemaInput),
  async (c) => {
    return c.json({ message: "Cliente autocadastrado" }, 201);
  }
);

customerRoutes.put(
  "/",
  zValidator("json", updateCustomerSchema),
  async (c) => {
    try {
      const updateProfileData = c.req.valid("json");

      const response = await fetch(
      `${orchestratorServiceUrl}/api/saga/client`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            data: updateProfileData,
        }),
      });

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
      console.error("Erro ao disparar saga de editar:", error);
      return c.json({ error: "Serviço de orquestração indisponível" }, 503);
    }
  }
);

customerRoutes.post(
  "/:cpf/aprovar",
  zValidator("param", aproveCustomerByCPFSchemaInput),
  async (c) => {
    return c.json({ message: "Cliente aprovado com sucesso" }, 200);
  }
);

customerRoutes.post(
  "/:cpf/reprovar",
  zValidator("param", declineCustomerByCPFSchemaInput),
  async (c) => {
    return c.json({ message: "Cliente reprovado com sucesso" }, 200);
  }
);
