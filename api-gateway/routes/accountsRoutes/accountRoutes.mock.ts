export const AccountBalanceMock = {
  cliente: "76179646090",
  conta: 123456,
  saldo: 1500.75,
};

export const AccountTransactionMock = {
  conta: "1291",
  data: "2023-10-01T10:00:00Z",
  destino: "4321",
  saldo: 1044.23,
  valor: 87.12,
};

export const ExtratoMock = {
  conta: "8722",
  saldo: 1933.32,
  movimentacoes: [
    {
      data: "2025-08-01T10:22:45-03:00",
      tipo: "saque",
      origem: "8872",
      destino: "9961",
      valor: 972.22
    }
  ]
};
