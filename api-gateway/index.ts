import { Hono } from "hono";
import { config } from "dotenv";
import { serve } from "@hono/node-server";
import { cors } from "hono/cors";
import { logger } from "hono/logger";
import { authMiddleware } from "./middleware/auth";
import { authRoutes } from "./routes/authRoutes/authRoutes";
import { customerRoutes } from "./routes/customerRoutes/customerRoutes";
import { accountRoutes } from "./routes/accountsRoutes/accountRoutes";
import { managerRoutes } from "./routes/managerRoutes/managerRoutes";
import { rebootRoutes } from "./routes/rebootRoutes/rebootRoutes";
import { AppVariables } from "./types/context";
import { existsSync } from "fs";
import { resolve } from "path";

const envLocalPath = resolve(process.cwd(), ".env.local");

if (existsSync(envLocalPath)) {
  config({ path: envLocalPath });
} else {
  config();
}

const app = new Hono<{ Variables: AppVariables }>();
app.use("*", cors());
app.use("*", logger());

app.get("/", (c) => c.text("Bantads API Gateway"));

app.route("/reboot", rebootRoutes);
app.route("/", authRoutes);
app.route("/clientes", customerRoutes);

app.use("/contas/*", authMiddleware);
app.use("/gerentes/*", authMiddleware);

app.route("/contas", accountRoutes);
app.route("/gerentes", managerRoutes);

app.onError((err, c) => {
  console.error("Gateway Error:", err);
  return c.json({ error: "Erro interno do servidor" }, 500);
});

app.notFound((c) => {
  return c.json({ error: "Rota não encontrada" }, 404);
});

const port = 3030;

serve(
  {
    fetch: app.fetch,
    port,
  },
  (info) => {
    console.log(`🚀 Server running at http://localhost:${info.port}`);
  }
);
