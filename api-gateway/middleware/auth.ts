import { Context, Next } from "hono";
import { decode } from "hono/jwt";
import { AppVariables } from "../types/context";

export const authMiddleware = async (
  c: Context<{ Variables: AppVariables }>,
  next: Next
) => {
  const token = c.req.header("Authorization");

  if (!token || !token.startsWith("Bearer ")) {
    return c.json({ error: "No access token provided" }, 401);
  }

  const jwt = token.replace("Bearer ", "").trim();

  if (!jwt || jwt.length === 0) {
    return c.json({ error: "No access token provided" }, 401);
  }

  try {
    const { payload } = decode(jwt);

    c.set("token", jwt);
    c.set("jwtPayload", payload);
  } catch (error) {
    return c.json({ error: "Invalid token format" }, 401);
  }

  await next();
};
