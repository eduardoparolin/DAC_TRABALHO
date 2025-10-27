import { Hono } from "hono";
import { zValidator } from "@hono/zod-validator";
import {
  AccountBalanceMock,
  AccountTransactionMock,
  ExtratoMock,
} from "./accountRoutes.mock";
import {
  ContaParamSchema,
  DepositarSacarSchemaInput,
  TransferirSchema,
} from "./accountRoutesSchema";

export const accountRoutes = new Hono();

accountRoutes.post(
  "/:numero/saldo",
  zValidator("param", ContaParamSchema),
  async (c) => {
    return c.json(AccountBalanceMock, 200);
  }
);

accountRoutes.post(
  "/:numero/depositar",
  zValidator("param", ContaParamSchema),
  zValidator("json", DepositarSacarSchemaInput),
  async (c) => {
    return c.json(AccountBalanceMock, 200);
  }
);

accountRoutes.post(
  "/:numero/sacar",
  zValidator("param", ContaParamSchema),
  zValidator("json", DepositarSacarSchemaInput),
  async (c) => {
    return c.json(AccountBalanceMock, 200);
  }
);

accountRoutes.post(
  "/:numero/transferir",
  zValidator("param", ContaParamSchema),
  zValidator("json", TransferirSchema),
  async (c) => {
    return c.json(AccountTransactionMock, 200);
  }
);

accountRoutes.post(
  "/:numero/extrato",
  zValidator("param", ContaParamSchema),
  zValidator("json", TransferirSchema),
  async (c) => {
    return c.json(ExtratoMock, 200);
  }
);
