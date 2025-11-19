import {Manager} from '../managers/manager.model';
import {GetManagersAdminDashboardResponse, GetManagersResponse} from '../managers/manager.types';
import {Client} from '../clients/client.model';

export class AdminDashboardManager {
  clientCount: number;
  netPositive: number;
  netNegative: number;
  manager: GetManagersResponse;

  constructor(clientCount: number, netPositive: number, netNegative: number, manager: GetManagersResponse) {
    this.clientCount = clientCount;
    this.netPositive = netPositive;
    this.netNegative = netNegative;
    this.manager = manager;
  }

  static fromJson(json: GetManagersAdminDashboardResponse): AdminDashboardManager {
    const clientCount = json['clientes'] ? json['clientes'].length : 0;
    const netPositive = json.saldo_positivo ? json.saldo_positivo : 0;
    const netNegative = json.saldo_negativo ? json.saldo_negativo : 0;
    return new AdminDashboardManager(clientCount, netPositive, netNegative, json['gerente']);
  }
}
