import { Context, Next } from "hono";
import { decode } from "hono/jwt";
import { AppVariables } from "../types/context";

export const authMiddleware = async (
  c: Context<{ Variables: AppVariables }>,
  next: Next
) => {
  const token = c.req.header("Authorization");
  const authServiceUrl = process.env.AUTH_SERVICE_URL;

  if (!token || !token.startsWith("Bearer ")) {
    return c.json({ error: "No access token provided" }, 401);
  }

  const jwt = token.replace("Bearer ", "").trim();

  if (!jwt || jwt.length === 0) {
      console.log('No access token provided');
    return c.json({ error: "No access token provided" }, 401);
  }

  if (!authServiceUrl) {
      console.log('Missing auth service configuration');
      return c.json({ error: "Missing auth service configuration" }, 500);
  }

  try {
    const response = await fetch(`${authServiceUrl}/auth/token/status`, {
      method: "GET",
      headers: {
        Authorization: token,
      },
    });

    if (!response.ok) {
        console.log("Unable to validate token");
        return c.json({ error: "Unable to validate token" }, 401);
    }

    const status = await response.json();

    if (status?.revoked) {
        return c.json({ error: "Token revoked" }, 401);
    }
  } catch (error) {
      console.log("Token validation service unavailable");
      return c.json({ error: "Token validation service unavailable" }, 503);
  }

  try {
    const { payload } = decode(jwt);

    c.set("token", jwt);
    c.set("jwtPayload", payload);
  } catch (error) {
      console.log("Invalid token format");
      return c.json({ error: "Invalid token format" }, 401);
  }

  await next();
};
