export type JwtPayload = {
  userId?: string;
  email?: string;
  role?: string;
  roles?: string[];
  [key: string]: any;
};

export type AppVariables = {
  token: string;
  jwtPayload: JwtPayload;
};
