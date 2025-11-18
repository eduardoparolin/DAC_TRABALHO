import {Hono} from "hono";
import {zValidator} from "@hono/zod-validator";
import {
    aproveCustomerByCPFSchemaInput,
    createCustomerSchemaInput,
    declineCustomerByCPFSchemaInput,
    declineCustomerBodySchema,
    getCustomerByCPFSchemaInput,
    getCustomersSchemaInput,
    UpdateCustomerByCPFSchemaInput,
    updateCustomerSchemaInput,
} from "./customerRoutesSchema";
import {z} from "zod";
import {AppVariables} from "../../types/context";
import {fetchWithAuth} from "../../utils/fetchWithAuth";
import {authMiddleware} from "../../middleware/auth";
import {requireManagerOrAdmin, checkRole} from "../../middleware/roleCheck";

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
        const {filtro, managerId, cpf, name} = c.req.valid("query");

        // R12: When no filter provided, list all approved clients
        if (!filtro) {
            const roleValidation = checkRole(
                c,
                "ROLE_GERENTE",
                "ROLE_ADMINISTRADOR"
            );
            if (!roleValidation.authorized) {
                return roleValidation.response;
            }

            const {clientServiceUrl} = getServiceUrls();

            // Build URL - if managerId is provided, filter by manager, otherwise get all
            let url: string;
            if (managerId) {
                // Filter by specific manager
                url = `${clientServiceUrl}/clientes/manager/${managerId}`;
                const params = new URLSearchParams();
                if (cpf) params.append("cpf", cpf);
                if (name) params.append("name", name);
                const queryString = params.toString();
                if (queryString) url += `?${queryString}`;
            } else {
                // Get all approved clients
                url = `${clientServiceUrl}/clientes/status/APROVADO`;
                const params = new URLSearchParams();
                if (cpf) params.append("cpf", cpf);
                if (name) params.append("name", name);
                const queryString = params.toString();
                if (queryString) url += `?${queryString}`;
            }

            const clientsResponse = await fetchWithAuth(c, url);

            if (!clientsResponse.ok) {
                return c.json(
                    {error: "Erro ao buscar clientes"},
                    clientsResponse.status as any
                );
            }

            const clients = await clientsResponse.json();

            // Fetch accounts to get managerId from each account
            const {bankAccountServiceUrl} = getServiceUrls();
            const accountNumbers = Array.from(
                new Set(
                    clients
                        .map((client: any) => client.accountId)
                        .filter((accountId: any) => !!accountId)
                        .map((accountId: any) => String(accountId))
                )
            );

            let accountsMap = new Map<string, any>();
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

                if (accountsResponse.ok) {
                    const accounts = await accountsResponse.json();
                    accounts.forEach((account: any) => {
                        if (account?.numero) {
                            accountsMap.set(String(account.numero), account);
                        }
                    });
                }
            }

            const clientsMapped = clients.map((client: any) => {
                const account = client.accountId ? accountsMap.get(String(client.accountId)) : null;
                return {
                    cpf: client.cpf,
                    nome: client.name,
                    email: client.email,
                    salario: client.salary,
                    contaId: client.accountId,
                    gerenteId: account?.gerente ?? null, // Get managerId from account
                };
            });

            return c.json(clientsMapped, 200);
        }

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

                const {clientServiceUrl} = getServiceUrls();

                const jwtPayload = c.get("jwtPayload");
                const userEmail = jwtPayload?.email;

                if (!userEmail) {
                    return c.json({error: "Email não encontrado no token"}, 400);
                }

                let url = `${clientServiceUrl}/clientes/status/AGUARDANDO_APROVACAO`;

                const clientsResponse = await fetchWithAuth(c, url);

                if (!clientsResponse.ok) {
                    return c.json(
                        {error: "Erro ao buscar clientes para aprovar"},
                        clientsResponse.status as any
                    );
                }

                const clients = await clientsResponse.json();

                // Fetch accounts to get managerId from each account
                const {bankAccountServiceUrl: bankAcctUrl} = getServiceUrls();
                const accountNumbers = Array.from(
                    new Set(
                        clients
                            .map((client: any) => client.accountId)
                            .filter((accountId: any) => !!accountId)
                            .map((accountId: any) => String(accountId))
                    )
                );

                let accountsMap = new Map<string, any>();
                if (accountNumbers.length > 0) {
                    const accountsResponse = await fetchWithAuth(
                        c,
                        `${bankAcctUrl}/query/contas/buscar?incluirTransacoes=false`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                            },
                            body: JSON.stringify({ accountNumbers }),
                        }
                    );

                    if (accountsResponse.ok) {
                        const accounts = await accountsResponse.json();
                        accounts.forEach((account: any) => {
                            if (account?.numero) {
                                accountsMap.set(String(account.numero), account);
                            }
                        });
                    }
                }

                const clientsMapped = clients.map((client: any) => {
                    const account = client.accountId ? accountsMap.get(String(client.accountId)) : null;
                    return {
                        cpf: client.cpf,
                        nome: client.name,
                        email: client.email,
                        salario: client.salary,
                        contaId: client.accountId,
                        gerenteId: account?.gerente ?? null, // Get managerId from account
                    };
                });

                return c.json(clientsMapped, 200);
            }

            case "adm_relatorio_clientes": {
                const roleValidation = checkRole(c, "ROLE_ADMINISTRADOR");
                if (!roleValidation.authorized) {
                    return roleValidation.response;
                }

                const {clientServiceUrl, bankAccountServiceUrl, managerServiceUrl} =
                    getServiceUrls();

                const clientsResponse = await fetchWithAuth(
                    c,
                    `${clientServiceUrl}/clientes`
                );
                if (!clientsResponse.ok) {
                    return c.json(
                        {error: "Erro ao buscar clientes"},
                        clientsResponse.status as any
                    );
                }

                const clients = await clientsResponse.json();

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
                        body: JSON.stringify({accountNumbers}),
                    }
                );

                if (!accountsResponse.ok) {
                    return c.json(
                        {error: "Erro ao buscar contas"},
                        accountsResponse.status as any
                    );
                }

                const accounts = await accountsResponse.json();

                // Create account map for quick lookup
                const accountMap = new Map<string, any>();
                accounts.forEach((account: any) => {
                    if (account?.numero) {
                        accountMap.set(String(account.numero), account);
                    }
                });

                // Extract manager IDs from accounts (not from clients)
                const managerIds = Array.from(
                    new Set(
                        accounts
                            .map((acc: any) => acc.gerente)
                            .filter((managerId: any) => managerId != null)
                    )
                );

                const managersResponse = await fetchWithAuth(
                    c,
                    `${managerServiceUrl}/manager/gerentes`,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({managerIds}),
                    }
                );

                if (!managersResponse.ok) {
                    return c.json(
                        {error: "Erro ao buscar gerentes"},
                        managersResponse.status as any
                    );
                }

                const managers = await managersResponse.json();

                const clientsWithAccountsAndManagers = clients.map((client: any) => {
                    const account = client.accountId ? accountMap.get(String(client.accountId)) : null;
                    // Find manager by the managerId from the account (not from client)
                    const managerId = account?.gerente;
                    const manager = managerId ? managers.find((mgr: any) => mgr.id === managerId) : null;
                    return {
                        ...client,
                        conta: account || null,
                        gerente: manager || null,
                    };
                });
                const clis = clientsWithAccountsAndManagers.map((client: any) => {
                    return {
                        ...client,
                        nome: client.name,
                        salario: client.salario,
                        rua: client.street,
                    }
                }).filter((client: any) => !!client.approvalDate);
                clis.sort((a: any, b: any) => a.nome.localeCompare(b.nome));
                return c.json(clis, 200);
            }

            case "melhores_clientes": {
                const {bankAccountServiceUrl, clientServiceUrl} = getServiceUrls();
                const response = await fetchWithAuth(
                    c,
                    `${bankAccountServiceUrl}/query/contas/top3`
                );

                if (!response.ok) {
                    return c.json(
                        {error: "Erro ao buscar melhores clientes"},
                        response.status as any
                    );
                }

                const accounts = await response.json();

                // Enrich with client data (CPF, nome, cidade, estado)
                const enrichedAccounts = await Promise.all(
                    accounts.map(async (account: any) => {
                        try {
                            const clientResponse = await fetchWithAuth(
                                c,
                                `${clientServiceUrl}/clientes/${account.cliente}`
                            );

                            if (clientResponse.ok) {
                                const clientData = await clientResponse.json();
                                return {
                                    cpf: clientData.cpf,
                                    nome: clientData.name,
                                    cidade: clientData.city,
                                    estado: clientData.state,
                                    saldo: account.saldo,
                                };
                            }
                        } catch (error) {
                            console.error(`Error fetching client ${account.cliente}:`, error);
                        }
                        // Fallback if client data cannot be fetched
                        return {
                            cpf: null,
                            nome: null,
                            cidade: null,
                            estado: null,
                            saldo: account.saldo,
                        };
                    })
                );

                return c.json(enrichedAccounts, 200);
            }

            default: {
                return c.json({error: "Filtro inválido"}, 400);
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
        const {clientServiceUrl} = getServiceUrls();

        try {
            const clientResponse = await fetchWithAuth(
                c,
                `${clientServiceUrl}/clientes/cpf/${cpf}`
            );

            if (!clientResponse.ok) {
                if (clientResponse.status === 404) {
                    return c.json({error: "Cliente não encontrado"}, 404);
                }
                return c.json(
                    {error: "Erro ao buscar dados do cliente"},
                    clientResponse.status as any
                );
            }

            const clientData = await clientResponse.json();

            let accountData = null;
            if (clientData.accountId) {
                try {
                    const {bankAccountServiceUrl} = getServiceUrls();
                    const accountUrl = `${bankAccountServiceUrl}/query/contas/${clientData.accountId}`;
                    const accountResponse = await fetchWithAuth(
                        c,
                        accountUrl
                    );
                    if (accountResponse.ok) {
                        accountData = await accountResponse.json();
                    } else {
                        console.log('Account fetch failed:', await accountResponse.text());
                    }
                } catch (error) {
                    console.error("Erro ao buscar a conta bancária:", error);
                }
            } else {
                console.log('No accountId in client data');
            }

            let managerData = null;
            if (clientData.managerId) {
                try {
                    const {managerServiceUrl} = getServiceUrls();
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
                saldo: accountData?.saldo || 0,
                limite: accountData?.limite || null,
                gerente: managerData?.cpf || null,
                gerente_nome: managerData?.name || null,
                gerente_email: managerData?.email || null,
            };
            console.log(composedResponse);
            return c.json(composedResponse, 200);
        } catch (error) {
            console.error("Erro ao buscar dados do cliente:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

customerRoutes.put(
    "/:cpf",
    authMiddleware,
    zValidator("param", UpdateCustomerByCPFSchemaInput),
    zValidator("json", updateCustomerSchemaInput),
    async (c) => {
        try {
            const cpf = c.req.param("cpf");
            const updateData = c.req.valid("json");
            const {clientServiceUrl, orchestratorServiceUrl} = getServiceUrls();

            // Get current client to retrieve clientId
            const clientResponse = await fetchWithAuth(
                c,
                `${clientServiceUrl}/clientes/cpf/${cpf}`
            );

            if (!clientResponse.ok) {
                return c.json({error: "Cliente não encontrado"}, 404);
            }

            const clientData = await clientResponse.json();

            // Map data to English
            const mappedData = {
                cpf: updateData.cpf,
                name: updateData.nome,
                email: updateData.email,
                phone: updateData.telefone,
                salary: updateData.salario,
                street: updateData.endereco,
                number: updateData.numero || "",
                complement: updateData.complemento || "",
                zipCode: updateData.CEP,
                city: updateData.cidade,
                state: updateData.estado,
                clientId: clientData.id,
            };

            // Trigger update saga via orchestrator
            const response = await fetchWithAuth(
                c,
                `${orchestratorServiceUrl}/api/saga/client`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        action: "UPDATE_CLIENT",
                        data: mappedData,
                    }),
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {
                        error: "Falha ao atualizar cliente",
                        details: errorData,
                    },
                    response.status as any
                );
            }

            // Wait for saga to complete
            await new Promise(resolve => setTimeout(resolve, 1500));

            // Fetch updated client data with account and manager info
            const {bankAccountServiceUrl, managerServiceUrl} = getServiceUrls();

            const updatedClientResponse = await fetchWithAuth(
                c,
                `${clientServiceUrl}/clientes/cpf/${cpf}`
            );

            if (!updatedClientResponse.ok) {
                return c.json({error: "Erro ao buscar dados atualizados"}, 500);
            }

            const updatedClient = await updatedClientResponse.json();

            // Fetch account data to get saldo and limite
            let accountData = null;
            if (updatedClient.accountId) {
                try {
                    const accountUrl = `${bankAccountServiceUrl}/query/contas/${updatedClient.accountId}`;
                    const accountResponse = await fetchWithAuth(c, accountUrl);
                    if (accountResponse.ok) {
                        accountData = await accountResponse.json();
                    }
                } catch (error) {
                    console.error("Erro ao buscar a conta bancária:", error);
                }
            }

            // Fetch manager data to get CPF
            let managerData = null;
            if (updatedClient.managerId) {
                try {
                    const managerResponse = await fetchWithAuth(
                        c,
                        `${managerServiceUrl}/manager/id/${updatedClient.managerId}`
                    );
                    if (managerResponse.ok) {
                        managerData = await managerResponse.json();
                    }
                } catch (error) {
                    console.error("Erro ao buscar dados do gerente:", error);
                }
            }

            // Return updated profile data with saldo, limite, and gerente
            return c.json({
                cpf: updatedClient.cpf,
                nome: updatedClient.name,
                email: updatedClient.email,
                salario: updatedClient.salary,
                saldo: accountData?.saldo || null,
                limite: accountData?.limite || null,
                gerente: managerData?.cpf || null,
            }, 200);
        } catch (error) {
            console.error("Erro ao atualizar cliente:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

customerRoutes.post(
    "/",
    zValidator("json", createCustomerSchemaInput),
    async (c) => {
        try {
            const clientData = c.req.valid("json");
            const {clientServiceUrl, orchestratorServiceUrl} = getServiceUrls();

            // Verificar se o CPF já existe
            const cpfCheckResponse = await fetch(
                `${clientServiceUrl}/clientes/existe/${clientData.cpf}`
            );

            if (cpfCheckResponse.ok) {
                const {clienteExiste} = await cpfCheckResponse.json();
                if (clienteExiste) {
                    return c.json({error: "CPF já cadastrado"}, 409);
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
            return c.json({error: "Serviço de orquestração indisponível"}, 503);
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
            const {clientServiceUrl, orchestratorServiceUrl} = getServiceUrls();

            // First, get client data to retrieve clientId, managerId, and accountId
            const clientResponse = await fetchWithAuth(
                c,
                `${clientServiceUrl}/clientes/cpf/${cpf}`
            );

            if (!clientResponse.ok) {
                return c.json(
                    {error: "Cliente não encontrado"},
                    404
                );
            }

            const clientData = await clientResponse.json();

            // Trigger approval saga via orchestrator
            const response = await fetchWithAuth(
                c,
                `${orchestratorServiceUrl}/api/saga/client/approve`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        action: "APPROVE_CLIENT",
                        data: {
                            cpf: cpf,
                            clientId: clientData.id,
                            managerId: clientData.managerId,
                            accountId: clientData.accountId,
                        },
                    }),
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {
                        error: "Falha ao iniciar aprovação",
                        details: errorData,
                    },
                    response.status as any
                );
            }

            const sagaData = await response.json();

            return c.json(
                {
                    message: "Aprovação iniciada com sucesso. Senha será enviada por email.",
                    sagaId: sagaData.sagaId,
                    cpf: cpf,
                },
                200
            );
        } catch (error) {
            console.error("Erro ao aprovar cliente:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
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
            const {usuario, motivo} = c.req.valid("json");
            const {clientServiceUrl} = getServiceUrls();

            const response = await fetchWithAuth(
                c,
                `${clientServiceUrl}/clientes/${cpf}/reprovar`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({rejectionReason: motivo}),
                }
            );

            if (!response.ok) {
                return c.json(
                    {error: "Erro ao reprovar cliente"},
                    response.status as any
                );
            }

            return c.json({message: "Cliente reprovado com sucesso"}, 200);
        } catch (error) {
            console.error("Erro ao reprovar cliente:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);
