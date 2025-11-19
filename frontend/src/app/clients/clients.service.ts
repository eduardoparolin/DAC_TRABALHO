import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {lastValueFrom} from 'rxjs';
import {ClientDetailsResponse, ClientReportResponse} from './clients.types';
import {Client, ClientJson} from './client.model';
import {ErrorHandlerService} from '../utils/error-handler.service';
import {SessionService} from '../session/session.service';

@Injectable({
  providedIn: 'root'
})
export class ClientsService {
  clients = signal<Client[]>([]);
  filteredClients = signal<Client[]>([]);
  http = inject(HttpClient);
  errorHandler = inject(ErrorHandlerService);
  loading = signal(false);
  sessionService = inject(SessionService);
  constructor() { }

  getClients(
    filtro: 'para_aprovar' | 'adm_relatorio_clientes' | 'melhores_clientes' | null,
    options: { managerId?: string } = {}
  ) {
    const params: Record<string, string> = {};
    if (filtro) {
      params['filtro'] = filtro;
    }
    if (options.managerId) {
      params['managerId'] = options.managerId;
    }
    return lastValueFrom(
      this.http.get<ClientReportResponse[]>(`${environment.baseUrl}/clientes`, { params })
    );
  }

  getClientByCpf(cpf: string) {
    return lastValueFrom(
      this.http.get<ClientDetailsResponse>(`${environment.baseUrl}/clientes/${cpf}`)
    );
  }

  async getAllClients() {
    this.loading.set(true);
    try {
      const clientsResponse = await this.getClients(null, {managerId: String(this.sessionService.user()?.id)});
      const clients = clientsResponse.map(client => this.mapClient(client));
      this.clients.set(clients);
      this.filteredClients.set(clients);
    } catch (error) {
      this.errorHandler.handleError(error as Error);
    } finally {
      this.loading.set(false);
    }
  }

  filterClients(filter: string) {
    const lowerFilter = filter.toLowerCase();
    this.filteredClients.set(this.clients().filter(client =>
      client.name.toLowerCase().includes(lowerFilter) ||
      client.cpf.includes(lowerFilter)
    ));
  }

  clearFilter() {
    this.filteredClients.set(this.clients());
  }

  private mapClient(client: ClientReportResponse): Client {
    const account = client.conta;
    const manager = client.gerente;
    const clientJson: ClientJson = {
      tipo: 'CLIENTE',
      usuario: {
        id: client.id,
        cpf: client.cpf,
        name: client.name,
        phone: client.phone ?? '',
        email: client.email,
      },
      saldo: account?.saldo ?? 0,
      limite: account?.limite ?? 0,
      salario: Number(client.salario) ?? 0,
      cidade: client.city,
      estado: client.state,
      telefone: client.phone,
      numero_conta: account?.numero ?? client.contaId,
      gerente: manager?.cpf,
      gerente_nome: manager?.name,
    };

    return Client.fromJson(clientJson);
  }
}
