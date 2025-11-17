import { Hono } from "hono";
import { zValidator } from "@hono/zod-validator";
import {
  aproveCustomerByCPFSchemaInput,
  createCustomerSchemaInput,
  declineCustomerByCPFSchemaInput,
  declineCustomerBodySchema,
  getCustomerByCPFSchemaInput,
  getCustomersSchemaInput,
} from "./customerRoutesSchema";
import { z } from "zod";
import { AppVariables } from "../../types/context";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import { authMiddleware } from "../../middleware/auth";
import { requireManagerOrAdmin, checkRole } from "../../middleware/roleCheck";

export const customerRoutes = new Hono<{ Variables: AppVariables }>();

const getServiceUrls = () => ({
  clientServiceUrl: process.env.CLIENT_SERVICE_URL,
  orchestratorServiceUrl: process.env.ORCHESTRATOR_SERVICE_URL,
  bankAccountServiceUrl: process.env.BANK_ACCOUNT_SERVICE_URL,
  managerServiceUrl: process.env.MANAGER_SERVICE_URL,
});

const mapClientDataToEnglish = (clientData: any) => {
  return {
    cpf: clientData.cpf,
    name: clientData.nome,
    email: clientData.email,
    phone: clientData.telefone,
    salary: clientData.salario,
    street: clientData.endereco,
    number: clientData.numero,
    complement: clientData.complemento || "",
    zipCode: clientData.CEP,
    city: clientData.cidade,
    state: clientData.estado,
  };
};

const updateCustomerSchema = z.object({
  cpf: z.string(),
  name: z.string(),
  email: z.string().email(),
  salary: z.number(),
  clientId: z.number(),
});

customerRoutes.get(
  "/",
  authMiddleware,
  zValidator("query", getCustomersSchemaInput),
  async (c) => {
    const { filtro, managerId } = c.req.valid("query");

    switch (filtro) {
      case "para_aprovar": {
        const roleValidation = checkRole(
          c,
          "ROLE_GERENTE",
          "ROLE_ADMINISTRADOR"
        );
        if (!roleValidation.authorized) {
          return roleValidation.response;
        }

        const { clientServiceUrl, managerServiceUrl } = getServiceUrls();

        const jwtPayload = c.get("jwtPayload");
        const userEmail = jwtPayload?.email;

        if (!userEmail) {
          return c.json({ error: "Email não encontrado no token" }, 400);
        }

        const managerResponse = await fetchWithAuth(
          c,
          `${managerServiceUrl}/manager/email/${userEmail}`
        );

        if (!managerResponse.ok) {
          return c.json(
            { error: "Erro ao buscar dados do gerente" },
            managerResponse.status as any
          );
        }

        const managerData = await managerResponse.json();
        const managerIdFromToken = managerData.id;
        const userRoles = jwtPayload?.roles || [];
        const isAdmin = userRoles.includes("ROLE_ADMINISTRADOR");
        const finalManagerId =
          isAdmin && managerId ? managerId : managerIdFromToken;

        let url = `${clientServiceUrl}/clientes/status/AGUARDANDO_APROVACAO`;
        if (finalManagerId) {
          url += `?managerId=${finalManagerId}`;
        }

        const clientsResponse = await fetchWithAuth(c, url);

        if (!clientsResponse.ok) {
          return c.json(
            { error: "Erro ao buscar clientes para aprovar" },
            clientsResponse.status as any
          );
        }

        const clients = await clientsResponse.json();

        const clientsMapped = clients.map((client: any) => ({
          cpf: client.cpf,
          nome: client.name,
          email: client.email,
          salario: client.salary,
          contaId: client.accountId,
          gerenteId: client.managerId,
        }));

        return c.json(clientsMapped, 200);
      }

      case "adm_relatorio_clientes": {
        const roleValidation = checkRole(c, "ROLE_ADMINISTRADOR");
        if (!roleValidation.authorized) {
          return roleValidation.response;
        }

        const { clientServiceUrl, bankAccountServiceUrl, managerServiceUrl } =
          getServiceUrls();

        const clientsResponse = await fetchWithAuth(
          c,
          `${clientServiceUrl}/clientes`
        );

        if (!clientsResponse.ok) {
          return c.json(
            { error: "Erro ao buscar clientes" },
            clientsResponse.status as any
          );
        }

        const clients = await clientsResponse.json();

        console.log(clients);

        const accountNumbers = clients
          .map((client: any) => client.accountId)
          .filter((accountId: any) => accountId != null)
          .map((accountId: any) => String(accountId));

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

        const accounts = await accountsResponse.json();

        const managerIds = clients
          .map((client: any) => client.managerId)
          .filter((managerId: any) => managerId != null);

        const managersResponse = await fetchWithAuth(
          c,
          `${managerServiceUrl}/manager/gerentes`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ managerIds }),
          }
        );

        if (!managersResponse.ok) {
          return c.json(
            { error: "Erro ao buscar gerentes" },
            managersResponse.status as any
          );
        }

        const managers = await managersResponse.json();

        const clientsWithAccountsAndManagers = clients.map((client: any) => {
          const account = accounts.find(
            (acc: any) => acc.numero === client.accountId
          );
          const manager = managers.find(
            (mgr: any) => mgr.id === client.managerId
          );
          return {
            ...client,
            conta: account || null,
            gerente: manager || null,
          };
        });

        return c.json(clientsWithAccountsAndManagers, 200);
      }

      case "melhores_clientes": {
        if (!managerId) {
          return c.json({ error: "ID do gerente não encontrado" }, 400);
        }

        const { bankAccountServiceUrl } = getServiceUrls();
        const response = await fetchWithAuth(
          c,
          `${bankAccountServiceUrl}/query/contas/${managerId}/gerente/top3`
        );

        if (!response.ok) {
          return c.json(
            { error: "Erro ao buscar melhores clientes" },
            response.status as any
          );
        }

        const data = await response.json();
        return c.json(data, 200);
      }

      default: {
        return c.json({ error: "Filtro inválido" }, 400);
      }
    }
  }
);

customerRoutes.get(
  "/:cpf",
  authMiddleware,
  zValidator("param", getCustomerByCPFSchemaInput),
  async (c) => {
    const cpf = c.req.param("cpf");
    const { clientServiceUrl } = getServiceUrls();

    try {
      const clientResponse = await fetchWithAuth(
        c,
        `${clientServiceUrl}/clientes/cpf/${cpf}`
      );

      if (!clientResponse.ok) {
        if (clientResponse.status === 404) {
          return c.json({ error: "Cliente não encontrado" }, 404);
        }
        return c.json(
          { error: "Erro ao buscar dados do cliente" },
          clientResponse.status as any
        );
      }

      const clientData = await clientResponse.json();

      let accountData = null;
      if (clientData.accountId) {
        try {
          const { bankAccountServiceUrl } = getServiceUrls();
          const accountResponse = await fetchWithAuth(
            c,
            `${bankAccountServiceUrl}/contas/${clientData.accountId}`
          );
          if (accountResponse.ok) {
            accountData = await accountResponse.json();
          }
        } catch (error) {
          console.error("Erro ao buscar a conta bancária:", error);
        }
      }

      let managerData = null;
      if (clientData.managerId) {
        try {
          const { managerServiceUrl } = getServiceUrls();
          const managerResponse = await fetchWithAuth(
            c,
            `${managerServiceUrl}/manager/id/${clientData.managerId}`
          );
          if (managerResponse.ok) {
            managerData = await managerResponse.json();
          }
        } catch (error) {
          console.error("Erro ao buscar dados do gerente:", error);
        }
      }

      const composedResponse = {
        cpf: clientData.cpf,
        nome: clientData.name,
        telefone: clientData.phone,
        email: clientData.email,
        endereco: `${clientData.street}, nr ${clientData.number}${
          clientData.complement ? ", " + clientData.complement : ""
        }`,
        cidade: clientData.city,
        estado: clientData.state,
        salario: clientData.salary,
        conta: accountData?.numero?.toString() || null,
        saldo: accountData?.saldo?.toString() || null,
        limite: accountData?.limite || null,
        gerente: managerData?.cpf || null,
        gerente_nome: managerData?.name || null,
        gerente_email: managerData?.email || null,
      };

      return c.json(composedResponse, 200);
    } catch (error) {
      console.error("Erro ao buscar dados do cliente:", error);
      return c.json({ error: "Erro interno do servidor" }, 500);
    }
  }
);

customerRoutes.post(
  "/",
  zValidator("json", createCustomerSchemaInput),
  async (c) => {
    try {
      const clientData = c.req.valid("json");
      const { clientServiceUrl, orchestratorServiceUrl } = getServiceUrls();

      // Verificar se o CPF já existe
      const cpfCheckResponse = await fetch(
        `${clientServiceUrl}/clientes/existe/${clientData.cpf}`
      );

      if (cpfCheckResponse.ok) {
        const { clienteExiste } = await cpfCheckResponse.json();
        if (clienteExiste) {
          return c.json({ error: "CPF já cadastrado" }, 409);
        }
      }

      const mappedData = mapClientDataToEnglish(clientData);

      const response = await fetchWithAuth(
        c,
        `${orchestratorServiceUrl}/api/saga/client/create`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            action: "CREATE_CLIENT",
            data: mappedData,
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
          cpf: clientData.cpf,
          email: clientData.email,
        },
        201
      );
    } catch (error) {
      console.error("Erro ao disparar saga de cadastro:", error);
      return c.json({ error: "Serviço de orquestração indisponível" }, 503);
    }
  }
);

customerRoutes.post(
  "/:cpf/aprovar",
  authMiddleware,
  requireManagerOrAdmin,
  zValidator("param", aproveCustomerByCPFSchemaInput),
  async (c) => {
    try {
      const cpf = c.req.param("cpf");
      const { clientServiceUrl, bankAccountServiceUrl } = getServiceUrls();

      const response = await fetchWithAuth(
        c,
        `${clientServiceUrl}/clientes/${cpf}/aprovar`,
        {
          method: "POST",
        }
      );

      if (!response.ok) {
        return c.json(
          { error: "Erro ao aprovar cliente" },
          response.status as any
        );
      }

      const approvalData = await response.json();
      const clientId = approvalData.clientId;

      let accountLimit = null;
      try {
        const accountResponse = await fetchWithAuth(
          c,
          `${bankAccountServiceUrl}/query/contas/cliente/${clientId}`
        );

        if (accountResponse.ok) {
          const accountData = await accountResponse.json();
          accountLimit = accountData.limite;
        }
      } catch (error) {
        console.error("Erro ao buscar limite da conta:", error);
      }

      return c.json(
        {
          message: "Cliente aprovado com sucesso",
          clientId: clientId,
          limite: accountLimit,
        },
        200
      );
    } catch (error) {
      console.error("Erro ao aprovar cliente:", error);
      return c.json({ error: "Erro interno do servidor" }, 500);
    }
  }
);

customerRoutes.post(
  "/:cpf/rejeitar",
  authMiddleware,
  requireManagerOrAdmin,
  zValidator("param", declineCustomerByCPFSchemaInput),
  zValidator("json", declineCustomerBodySchema),
  async (c) => {
    try {
      const cpf = c.req.param("cpf");
      const { rejectionReason } = c.req.valid("json");
      const { clientServiceUrl } = getServiceUrls();

      const response = await fetchWithAuth(
        c,
        `${clientServiceUrl}/clientes/${cpf}/reprovar`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ rejectionReason }),
        }
      );

      if (!response.ok) {
        return c.json(
          { error: "Erro ao reprovar cliente" },
          response.status as any
        );
      }

      return c.json({ message: "Cliente reprovado com sucesso" }, 200);
    } catch (error) {
      console.error("Erro ao reprovar cliente:", error);
      return c.json({ error: "Erro interno do servidor" }, 500);
    }
  }
);
