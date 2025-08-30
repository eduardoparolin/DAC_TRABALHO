import {Manager} from '../managers/manager.model';
import {GetManagersAdminDashboardResponse} from '../managers/manager.types';
import {Client} from '../clients/client.model';

export class AdminDashboardManager {
  clientCount: number;
  netPositive: number;
  netNegative: number;
  manager: Manager;

  constructor(clientCount: number, netPositive: number, netNegative: number, manager: Manager) {
    this.clientCount = clientCount;
    this.netPositive = netPositive;
    this.netNegative = netNegative;
    this.manager = manager;
  }

  static fromJson(json: GetManagersAdminDashboardResponse): AdminDashboardManager {
    const manager = Manager.fromJson(json.gerente);
    manager.clientList = (json['clientes'] as any[])?.map(clientJson => new Client(clientJson['saldo'])) ?? [];
    const clientCount = json['clientes'] ? json['clientes'].length : 0;
    const netPositive = json.saldo_positivo ? json.saldo_positivo : 0;
    const netNegative = json.saldo_negativo ? json.saldo_negativo : 0;
    return new AdminDashboardManager(clientCount, netPositive, netNegative, manager);
  }
}
