import { Context, Next } from "hono";

export const authMiddleware = async (c: Context, next: Next) => {
  const token = c.req.header("Authorization");

  if (!token || !token.startsWith("Bearer ")) {
    return c.json(401);
  }

  const jwt = token.replace("Bearer ", "");

  c.set("token", jwt);

  await next();
};
