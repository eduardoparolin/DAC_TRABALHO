import 'dotenv/config'
import { Hono } from "hono";
import { serve } from "@hono/node-server";
import { cors } from "hono/cors";
import { logger } from "hono/logger";
import { authMiddleware } from "./middleware/auth";
import { authRoutes } from "./routes/authRoutes/authRoutes";
import { customerRoutes } from "./routes/customerRoutes/customerRoutes";
import { accountRoutes } from "./routes/accountsRoutes/accountRoutes";
import { managerRoutes } from "./routes/managerRoutes/managerRoutes";

const app = new Hono();
app.use("*", cors());
app.use("*", logger());

app.get("/", (c) => c.text("Bantads API Gateway"));

// app.use("/clientes/*", authMiddleware);
// app.use("/contas/*", authMiddleware);
// app.use("/gerentes/*", authMiddleware);

app.route("/auth", authRoutes);
app.route("/clientes", customerRoutes);
app.route("/contas", accountRoutes);
app.route("/gerentes", managerRoutes);

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
