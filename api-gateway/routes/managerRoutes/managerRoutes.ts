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
