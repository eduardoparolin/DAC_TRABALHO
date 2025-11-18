import {Hono, Context} from "hono";
import {zValidator} from "@hono/zod-validator";
import {
    movementRequestSchema,
    transferRequestSchema,
    accountNumberParamSchema,
    managerIdParamSchema,
    clientIdParamSchema,
    accountsRequestSchema,
    includeTransactionsQuerySchema,
} from "./accountRoutesSchema";
import {AppVariables} from "../../types/context";
import {fetchWithAuth} from "../../utils/fetchWithAuth";
import {authMiddleware} from "../../middleware/auth";
import {
    requireClient,
    requireManagerOrAdmin,
    requireClientOrAdmin,
} from "../../middleware/roleCheck";

export const accountRoutes = new Hono<{ Variables: AppVariables }>();

const getServiceUrls = () => ({
    bankAccountServiceUrl: process.env.BANK_ACCOUNT_SERVICE_URL,
    clientServiceUrl: process.env.CLIENT_SERVICE_URL,
});

const enrichBalanceResponse = async (
    c: Context<{ Variables: AppVariables }>,
    balanceData: any
) => {
    if (!balanceData) {
        return balanceData;
    }

    const {clientServiceUrl} = getServiceUrls();

    if (!clientServiceUrl) {
        return balanceData;
    }

    const clientId = balanceData.cliente;

    if (!clientId) {
        return balanceData;
    }

    try {
        const clientResponse = await fetchWithAuth(
            c,
            `${clientServiceUrl}/clientes/${clientId}`
        );

        if (!clientResponse.ok) {
            return balanceData;
        }

        const clientData = await clientResponse.json();
        if (clientData?.cpf) {
            return {
                ...balanceData,
                cliente: clientData.cpf,
            };
        }
    } catch (error) {
        console.error("Erro ao enriquecer saldo com CPF do cliente:", error);
    }

    return balanceData;
};

accountRoutes.post(
    "/:numero/depositar",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    zValidator("json", movementRequestSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {valor} = c.req.valid("json");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/command/contas/${numero}/deposito`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({valor}),
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao realizar depósito", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            const enrichedData = await enrichBalanceResponse(c, data);

            // enrichedData.saldo = String(enrichedData.saldo);
            // console.log(enrichedData);
            return c.json(enrichedData, 200);
        } catch (error) {
            console.error("Erro ao realizar depósito:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.post(
    "/:numero/sacar",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    zValidator("json", movementRequestSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {valor} = c.req.valid("json");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/command/contas/${numero}/sacar`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({valor}),
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao realizar saque", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            const enrichedData = await enrichBalanceResponse(c, data);
            return c.json(enrichedData, 200);
        } catch (error) {
            console.error("Erro ao realizar saque:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

// Transferência
accountRoutes.post(
    "/:numero/transferir",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    zValidator("json", transferRequestSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {valor, destino} = c.req.valid("json");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/command/contas/${numero}/transferir`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({valor, destino}),
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao realizar transferência", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            const enrichedData = await enrichBalanceResponse(c, data);
            return c.json(enrichedData, 200);
        } catch (error) {
            console.error("Erro ao realizar transferência:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.post(
    "/:numero/saldo",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${numero}/saldo`,
                {
                    method: "POST",
                }
            );

            if (!response.ok) {
                if (response.status === 404) {
                    return c.json({error: "Conta não encontrada"}, 404);
                }
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao consultar saldo", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            const enrichedData = await enrichBalanceResponse(c, data);
            return c.json(enrichedData, 200);
        } catch (error) {
            console.error("Erro ao consultar saldo:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.get(
    "/:numero/saldo",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${numero}/saldo`,
                {
                    method: "GET",
                }
            );

            if (!response.ok) {
                if (response.status === 404) {
                    return c.json({error: "Conta não encontrada"}, 404);
                }
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao consultar saldo", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            const enrichedData = await enrichBalanceResponse(c, data);
            return c.json(enrichedData, 200);
        } catch (error) {
            console.error("Erro ao consultar saldo:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.post(
    "/:numero/extrato",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${numero}/extrato`,
                {
                    method: "POST",
                }
            );

            if (!response.ok) {
                if (response.status === 404) {
                    return c.json({error: "Conta não encontrada"}, 404);
                }
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao consultar extrato", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            return c.json(data, 200);
        } catch (error) {
            console.error("Erro ao consultar extrato:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.get(
    "/:numero/extrato",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${numero}/extrato`,
                {
                    method: "GET",
                }
            );

            if (!response.ok) {
                if (response.status === 404) {
                    return c.json({error: "Conta não encontrada"}, 404);
                }
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao consultar extrato", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            const dP = {
                ...data, 'movimentacoes': data['movimentacoes'].map((m: any) => {
                    return {
                        ...m,
                        tipo: parseTipoMov(m.tipo),
                        data: parseData(m.data)
                    }
                })
            }
            return c.json(dP, 200);
        } catch (error) {
            console.error("Erro ao consultar extrato:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

const parseTipoMov = (tipo: string) => {
    if (tipo === 'DEPOSITO') {
        return 'depósito';
    }
    if (tipo === 'SAQUE') {
        return 'saque';
    }
    if (tipo === 'TRANSFERENCIA') {
        return 'transferência';
    }
    return tipo;
}

const parseData = (data: string) => {
    // Convert from '2025-11-18T12:07:14-03:00' to '2025-11-18T15:07:14Z' (UTC)
    const date = new Date(data);
    return date.toISOString().replace(/\.\d{3}Z$/, 'Z');
}

accountRoutes.get(
    "/:numero",
    authMiddleware,
    zValidator("param", accountNumberParamSchema),
    async (c) => {
        const {numero} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${numero}`
            );

            if (!response.ok) {
                if (response.status === 404) {
                    return c.json({error: "Conta não encontrada"}, 404);
                }
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao buscar detalhes da conta", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            return c.json(data, 200);
        } catch (error) {
            console.error("Erro ao buscar detalhes da conta:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.get(
    "/:idGerente/gerente",
    authMiddleware,
    zValidator("param", managerIdParamSchema),
    async (c) => {
        const {idGerente} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${idGerente}/gerente`
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao buscar contas do gerente", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            return c.json(data, 200);
        } catch (error) {
            console.error("Erro ao buscar contas do gerente:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.get(
    "/:idGerente/gerente/top3",
    authMiddleware,
    zValidator("param", managerIdParamSchema),
    async (c) => {
        const {idGerente} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${idGerente}/gerente/top3`
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao buscar top 3 contas", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            return c.json(data, 200);
        } catch (error) {
            console.error("Erro ao buscar top 3 contas:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.get(
    "/cliente/:idCliente",
    authMiddleware,
    zValidator("param", clientIdParamSchema),
    async (c) => {
        const {idCliente} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/cliente/${idCliente}`
            );

            if (!response.ok) {
                if (response.status === 404) {
                    return c.json(
                        {error: "Conta não encontrada para este cliente"},
                        404
                    );
                }
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao buscar conta do cliente", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            return c.json(data, 200);
        } catch (error) {
            console.error("Erro ao buscar conta do cliente:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.post(
    "/buscar",
    authMiddleware,
    zValidator("json", accountsRequestSchema),
    zValidator("query", includeTransactionsQuerySchema),
    async (c) => {
        const requestBody = c.req.valid("json");
        const {incluirTransacoes} = c.req.valid("query");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/buscar?incluirTransacoes=${incluirTransacoes}`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(requestBody),
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao buscar contas", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            return c.json(data, 200);
        } catch (error) {
            console.error("Erro ao buscar contas:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);

accountRoutes.get(
    "/:idGerente/gerente/contasAtivas",
    authMiddleware,
    zValidator("param", managerIdParamSchema),
    async (c) => {
        const {idGerente} = c.req.valid("param");
        const {bankAccountServiceUrl} = getServiceUrls();

        try {
            const response = await fetchWithAuth(
                c,
                `${bankAccountServiceUrl}/query/contas/${idGerente}/gerente/contasAtivas`
            );

            if (!response.ok) {
                const errorData = await response.json();
                return c.json(
                    {error: "Erro ao buscar contas ativas", details: errorData},
                    response.status as any
                );
            }

            const data = await response.json();
            return c.json(data, 200);
        } catch (error) {
            console.error("Erro ao buscar contas ativas:", error);
            return c.json({error: "Erro interno do servidor"}, 500);
        }
    }
);
