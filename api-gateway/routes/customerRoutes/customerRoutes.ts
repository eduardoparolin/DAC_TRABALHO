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

export const customerRoutes = new Hono();

customerRoutes.get(
  "/",
  zValidator("json", getCustomersSchemaInput, async (c) => {})
);

customerRoutes.get(
  "/:cpf",
  zValidator("json", getCustomerByCPFSchemaInput, async (c) => {})
);

customerRoutes.post(
  "/",
  zValidator("json", createCustomerSchemaInput),
  async (c) => {}
);

customerRoutes.put(
  "/:cpf",
  zValidator("json", UpdateCustomerByCPFSchemaInput, async (c) => {})
);

customerRoutes.post(
  "/:cpf/aprovar",
  zValidator("json", aproveCustomerByCPFSchemaInput),
  async (c) => {}
);

customerRoutes.post(
  "/:cpf/reprovar",
  zValidator("json", declineCustomerByCPFSchemaInput),
  async (c) => {}
);
