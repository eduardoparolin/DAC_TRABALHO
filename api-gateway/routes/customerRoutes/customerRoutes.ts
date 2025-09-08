import { Hono } from "hono";
import { zValidator } from "@hono/zod-validator";
import {
  aproveCustomerByCPFSchemaInput,
  createCustomerSchemaInput,
  declineCustomerByCPFSchemaInput,
  getCustomerByCPFSchemaInput,
  getCustomersSchemaInput,
  UpdateCustomerByCPFSchemaInput,
} from "./customerRoutesSchema";
import { customersMock } from "./customerRoutes.mock";

export const customerRoutes = new Hono();

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
  "/:cpf",
  zValidator("param", UpdateCustomerByCPFSchemaInput),
  async (c) => {
    return c.json({ message: "Perfil do cliente alterado com sucesso" }, 200);
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
