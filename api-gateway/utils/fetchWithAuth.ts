import { Context } from "hono";
import { AppVariables } from "../types/context";
s;
export const fetchWithAuth = async (
  c: Context<{ Variables: AppVariables }>,
  url: string,
  options?: RequestInit
): Promise<Response> => {
  const token = c.get("token");

  const headers = new Headers(options?.headers);
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  return fetch(url, {
    ...options,
    headers,
  });
};
