import { Hono } from "hono";
import { serve } from "@hono/node-server";
import { cors } from "hono/cors";
import { logger } from "hono/logger";
import { authRoutes } from "./routes/authRoutes";
import { customerRoutes } from "./routes/customerRoutes";
import { accountRoutes } from "./routes/accountRoutes";
import { managerRoutes } from "./routes/managerRoutes";
import { authMiddleware } from "./middleware/auth";

const app = new Hono();
app.use("*", cors());
app.use("*", logger());

app.get("/", (c) => c.text("Bantads API Gateway"));

app.route("/auth", authRoutes);
app.route("/clientes", customerRoutes);
app.route("/contas", accountRoutes);
app.route("/gerentes", managerRoutes);

app.use("/clientes/*", authMiddleware);
app.use("/contas/*", authMiddleware);
app.use("/gerentes/*", authMiddleware);

app.onError((err, c) => {
  console.error("Gateway Error:", err);
  return c.json({ error: "Erro interno do servidor" }, 500);
});

app.notFound((c) => {
  return c.json({ error: "Rota não encontrada" }, 404);
});

serve(app, (info) => {
  console.log(`🚀 Server running at http://localhost:${info.port}`);
});
