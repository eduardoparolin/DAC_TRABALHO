import { Hono, Context } from "hono";
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
import { authMiddleware } from "../../middleware/auth";
import { checkRole } from "../../middleware/roleCheck";

export const managerRoutes = new Hono<{ Variables: AppVariables }>();

const getServiceUrls = () => ({
  managerServiceUrl: process.env.MANAGER_SERVICE_URL,
  clientServiceUrl: process.env.CLIENT_SERVICE_URL,
  bankAccountServiceUrl: process.env.BANK_ACCOUNT_SERVICE_URL,
});

const normalizeNumber = (value: unknown): number => {
  const num =
    typeof value === "number"
      ? value
      : value !== undefined && value !== null
      ? Number(value)
      : 0;

  if (!Number.isFinite(num)) {
    return 0;
  }

  return Math.round((num + Number.EPSILON) * 100) / 100;
};

const buildManagersDashboard = async (
  c: Context<{ Variables: AppVariables }>
) => {
  const roleValidation = checkRole(c, "ROLE_ADMINISTRADOR");
  if (!roleValidation.authorized) {
    return roleValidation.response;
  }

  const { managerServiceUrl, clientServiceUrl, bankAccountServiceUrl } =
    getServiceUrls();

  if (!managerServiceUrl || !clientServiceUrl || !bankAccountServiceUrl) {
    return c.json({ error: "Serviços não configurados" }, 500);
  }

  try {
    const [managersResponse, clientsResponse] = await Promise.all([
      fetchWithAuth(c, `${managerServiceUrl}/manager`),
      fetchWithAuth(c, `${clientServiceUrl}/clientes`),
    ]);

    if (!managersResponse.ok) {
      return c.json(
        { error: "Erro ao buscar gerentes" },
        managersResponse.status as any
      );
    }

    if (!clientsResponse.ok) {
      return c.json(
        { error: "Erro ao buscar clientes" },
        clientsResponse.status as any
      );
    }

    const managers = await managersResponse.json();
    const clients = await clientsResponse.json();

    const accountNumbers = Array.from(
      new Set(
        clients
          .map((client: any) => client.accountId)
          .filter((accountId: any) => !!accountId)
          .map((accountId: any) => String(accountId))
      )
    );

    let accounts: any[] = [];

    if (accountNumbers.length > 0) {
      const accountsResponse = await fetchWithAuth(
        c,
        `${bankAccountServiceUrl}/query/contas/buscar?incluirTransacoes=false`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ accountNumbers }),
        }
      );

      if (!accountsResponse.ok) {
        return c.json(
          { error: "Erro ao buscar contas" },
          accountsResponse.status as any
        );
      }

      accounts = await accountsResponse.json();
    }

    const accountByNumber = new Map<string, any>();
    accounts.forEach((account: any) => {
      if (account?.numero) {
        accountByNumber.set(String(account.numero), account);
      }
    });

    const dashboard = managers.map((manager: any) => {
      const managerId =
        manager?.id !== undefined && manager?.id !== null
          ? Number(manager.id)
          : null;
      const managerClients =
        managerId === null
          ? []
          : clients.filter(
              (client: any) =>
                client.managerId !== null &&
                client.managerId !== undefined &&
                Number(client.managerId) === managerId &&
                client.approvalDate
            );

      let saldoPositivo = 0;
      let saldoNegativo = 0;

      const clientes = managerClients.map((client: any) => {
        const accountNumber = client.accountId
          ? String(client.accountId)
          : null;
        const account = accountNumber
          ? accountByNumber.get(accountNumber)
          : null;

        const saldo = normalizeNumber(account?.saldo ?? 0);

        if (saldo >= 0) {
          saldoPositivo += saldo;
        } else {
          saldoNegativo += saldo;
        }

        return {
          cliente: client.cpf,
          numero: accountNumber,
          saldo,
          limite: normalizeNumber(account?.limite ?? 0),
          gerente: account?.gerente ?? manager.cpf,
          criacao: account?.criacao ?? client.creationDate ?? null,
          status: account?.status ?? client.status ?? null,
        };
      });

      return {
        gerente: {
          id: manager.id,
          cpf: manager.cpf,
          nome: manager.name,
          email: manager.email,
          tipo: manager.type ?? manager.managerType ?? null,
        },
        clientes,
        saldo_positivo: normalizeNumber(saldoPositivo),
        saldo_negativo: normalizeNumber(saldoNegativo),
      };
    });

    dashboard.sort(
      (a: any, b: any) => b.saldo_positivo - a.saldo_positivo
    );

    return c.json(dashboard, 200);
  } catch (error) {
    console.error("Erro ao montar dashboard de gerentes:", error);
    return c.json({ error: "Erro interno do servidor" }, 500);
  }
};

managerRoutes.get(
  "/",
  authMiddleware,
  zValidator("query", getManagersSchema),
  async (c) => {
    const { filtro } = c.req.valid("query");

    if (filtro === "dashboard") {
      return buildManagersDashboard(c);
    }

    if (filtro) {
      return c.json({ error: "Filtro inválido" }, 400);
    }

    const { managerServiceUrl } = getServiceUrls();

    if (!managerServiceUrl) {
      return c.json({ error: "Serviço de gerente não configurado" }, 500);
    }

    try {
      const response = await fetchWithAuth(c, `${managerServiceUrl}/manager`);

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
  }
);

managerRoutes.post(
  "/",
  authMiddleware,
  zValidator("json", createManagerSchemaInput),
  async (c) => {
    const roleValidation = checkRole(c, "ROLE_ADMINISTRADOR");
    if (!roleValidation.authorized) {
      return roleValidation.response;
    }

    const body = c.req.valid("json");
    const { managerServiceUrl } = getServiceUrls();
    const orchestratorServiceUrl = process.env.ORCHESTRATOR_SERVICE_URL;

    if (!managerServiceUrl || !orchestratorServiceUrl) {
      return c.json({ error: "Serviços não configurados" }, 500);
    }

    try {
      // Step 1: Check if manager with CPF already exists (immediate 409 check)
      const checkResponse = await fetchWithAuth(
        c,
        `${managerServiceUrl}/manager/cpf/${body.cpf}`
      );

      if (checkResponse.ok) {
        // Manager already exists
        return c.json({ error: "Gerente com este CPF já existe" }, 409);
      }

      // Step 2: Start saga orchestration
      const sagaResponse = await fetchWithAuth(
        c,
        `${orchestratorServiceUrl}/api/saga/manager/create`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            data: {
              cpf: body.cpf,
              name: body.nome,
              email: body.email,
              password: body.senha,
              type: body.tipo,
            },
          }),
        }
      );

      if (!sagaResponse.ok) {
        const error = await sagaResponse.json();
        return c.json(
          { error: error.error || "Erro ao criar gerente" },
          sagaResponse.status as any
        );
      }

      const saga = await sagaResponse.json();
      const sagaId = saga.sagaId;

      // Step 3: Poll for saga completion (synchronous response)
      const maxAttempts = 60; // 30 seconds max (500ms * 60)
      let attempts = 0;
      let sagaCompleted = false;
      let sagaResult: any = null;

      while (attempts < maxAttempts && !sagaCompleted) {
        await new Promise((resolve) => setTimeout(resolve, 500)); // Wait 500ms

        const statusResponse = await fetchWithAuth(
          c,
          `${orchestratorServiceUrl}/api/saga/${sagaId}`
        );

        if (statusResponse.ok) {
          sagaResult = await statusResponse.json();

          if (sagaResult.status === "COMPLETED") {
            sagaCompleted = true;
          } else if (sagaResult.status === "FAILED") {
            return c.json(
              { error: "Erro ao criar gerente: Saga falhou" },
              500
            );
          }
        }

        attempts++;
      }

      if (!sagaCompleted) {
        return c.json(
          { error: "Timeout: A criação do gerente está demorando" },
          408
        );
      }

      // Step 4: Return created manager data
      const managerData = sagaResult.data;
      return c.json(
        {
          cpf: managerData.cpf,
          nome: managerData.name,
          email: managerData.email,
          tipo: managerData.type,
        },
        201
      );
    } catch (error) {
      console.error("Erro ao criar gerente:", error);
      return c.json({ error: "Erro interno do servidor" }, 500);
    }
  }
);

managerRoutes.get("/:cpf", zValidator("param", CPFParamSchema), async (c) => {
    const { managerServiceUrl } = getServiceUrls();
    const orchestratorServiceUrl = process.env.ORCHESTRATOR_SERVICE_URL;

    if (!managerServiceUrl || !orchestratorServiceUrl) {
        return c.json({ error: "Serviços não configurados" }, 500);
    }

    const { cpf } = c.req.param();

    const checkResponse = await fetchWithAuth(
        c,
        `${managerServiceUrl}/manager/cpf/${cpf}`
    );
    if (checkResponse.ok) {
        let gerente = await checkResponse.json();
        gerente = {...gerente, nome: gerente.name, tipo: gerente.type};
        return c.json(gerente, 200);
    }
  return c.json(await checkResponse.json(), checkResponse.status as any);
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
