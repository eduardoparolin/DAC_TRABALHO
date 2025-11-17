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
} from "./managerRoutes.mock";
import { AppVariables } from "../../types/context";
import { fetchWithAuth } from "../../utils/fetchWithAuth";

export const managerRoutes = new Hono<{ Variables: AppVariables }>();

const getServiceUrls = () => ({
  managerServiceUrl: process.env.MANAGER_SERVICE_URL,
});

managerRoutes.get("/", zValidator("query", getManagersSchema), async (c) => {
  const { managerServiceUrl } = getServiceUrls();

  try {
    const response = await fetchWithAuth(
      c,
      `${managerServiceUrl}/manager`
    );

    if (!response.ok) {
      return c.json(
        { error: "Erro ao buscar gerentes" },
        response.status as any
      );
    }

    const managers = await response.json();
    return c.json(managers, 200);
  } catch (error) {
    console.error("Erro ao buscar gerentes:", error);
    return c.json({ error: "Erro interno do servidor" }, 500);
  }
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

managerRoutes.get("/:managerId/clientes/status/:status", async (c) => {
  const { managerServiceUrl } = getServiceUrls();
  const { managerId, status } = c.req.param();

  try {
    const response = await fetchWithAuth(
      c,
      `${managerServiceUrl}/manager/${managerId}/clientes/status/${status}`
    );

    if (!response.ok) {
      return c.json(
        { error: "Erro ao buscar clientes por status e gerente" },
        response.status as any
      );
    }

    const clients = await response.json();
    return c.json(clients, 200);
  } catch (error) {
    console.error("Erro ao buscar clientes por status e gerente:", error);
    return c.json({ error: "Erro interno do servidor" }, 500);
  }
});
