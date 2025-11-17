import { Context, Next } from "hono";
import { AppVariables } from "../types/context";

export const requireRole = (...allowedRoles: string[]) => {
  return async (c: Context<{ Variables: AppVariables }>, next: Next) => {
    const jwtPayload = c.get("jwtPayload");

    if (!jwtPayload) {
      return c.json({ error: "Token não encontrado" }, 401);
    }

    const userRoles = jwtPayload.roles;

    if (!userRoles || !Array.isArray(userRoles) || userRoles.length === 0) {
      return c.json({ error: "Role não encontrada no token" }, 403);
    }

    const hasPermission = userRoles.some((role: string) =>
      allowedRoles.includes(role)
    );

    if (!hasPermission) {
      return c.json(
        {
          error: "Acesso negado",
          message: `Esta ação requer uma das seguintes roles: ${allowedRoles.join(
            ", "
          )}`,
        },
        403
      );
    }

    await next();
  };
};

export const requireAdmin = requireRole("ROLE_ADMINISTRADOR");

export const requireManager = requireRole("ROLE_GERENTE");

export const requireClient = requireRole("ROLE_CLIENTE");

export const requireManagerOrAdmin = requireRole(
  "ROLE_GERENTE",
  "ROLE_ADMINISTRADOR"
);

export const requireClientOrAdmin = requireRole(
  "ROLE_CLIENTE",
  "ROLE_ADMINISTRADOR"
);

export const checkRole = (
  c: Context<{ Variables: AppVariables }>,
  ...allowedRoles: string[]
): { authorized: boolean; response?: any } => {
  const jwtPayload = c.get("jwtPayload");

  if (!jwtPayload) {
    return {
      authorized: false,
      response: c.json({ error: "Token não encontrado" }, 401),
    };
  }

  const userRoles = jwtPayload.roles;

  if (!userRoles || !Array.isArray(userRoles) || userRoles.length === 0) {
    return {
      authorized: false,
      response: c.json({ error: "Role não encontrada no token" }, 403),
    };
  }

  const hasPermission = userRoles.some((role: string) =>
    allowedRoles.includes(role)
  );

  if (!hasPermission) {
    return {
      authorized: false,
      response: c.json(
        {
          error: "Acesso negado",
          message: `Esta ação requer uma das seguintes roles: ${allowedRoles.join(
            ", "
          )}`,
        },
        403
      ),
    };
  }

  return { authorized: true };
};
