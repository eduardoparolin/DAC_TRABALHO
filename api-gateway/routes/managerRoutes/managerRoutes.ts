import { Hono } from "hono";
import { zValidator } from "@hono/zod-validator";
import {
  CPFParamSchema,
  createManagerSchemaInput,
  getManagersSchema,
  updateManagerSchemaInput,
} from "./managerRoutesSchema";
import {
  createManagerMock,
  getManagerByCPFMock,
  getManagersMock,
} from "./managerRoutes.mock";

export const managerRoutes = new Hono();

managerRoutes.get("/", zValidator("query", getManagersSchema), async (c) => {
  return c.json(getManagersMock, 200);
});

managerRoutes.post(
  "/",
  zValidator("json", createManagerSchemaInput),
  async (c) => {
    return c.json(createManagerMock, 201);
  }
);

managerRoutes.get("/:cpf", zValidator("param", CPFParamSchema), async (c) => {
  return c.json(getManagerByCPFMock, 200);
});

managerRoutes.delete(
  "/:cpf",
  zValidator("param", CPFParamSchema),
  async (c) => {
    return c.json(getManagerByCPFMock, 200);
  }
);

managerRoutes.put(
  "/",
  zValidator("json", updateManagerSchemaInput),
  async (c) => {
    return c.json(createManagerMock, 200);
  }
);
