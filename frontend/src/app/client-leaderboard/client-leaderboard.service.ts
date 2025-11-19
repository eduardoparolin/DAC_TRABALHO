import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Client} from '../clients/client.model';
import {lastValueFrom} from 'rxjs';
import {environment} from '../../environments/environment';
import {SessionService} from '../session/session.service';
import {ErrorHandlerService} from '../utils/error-handler.service';

type TopAccountResponse = {
  cliente: string;
  numero: string;
  saldo: number;
  limite: number;
  gerente: string;
  criacao: string;
  status: string;
};

@Injectable({
  providedIn: 'root'
})
export class ClientLeaderboardService {

  http = inject(HttpClient);
  session = inject(SessionService);
  errorHandler = inject(ErrorHandlerService);
  clients = signal<Client[]>([]);
  constructor() { }

  async getAllClients() {
    const user = this.session.getUser();
    if (!user?.isManager()) {
      this.errorHandler.handleWarning('Apenas gerentes podem consultar o ranking de clientes.');
      this.clients.set([]);
      return;
    }

    try {
      const clientsResponse = await lastValueFrom(
        this.http.get<TopAccountResponse[]>(`${environment.baseUrl}/clientes`, {
          params: {
            filtro: 'melhores_clientes',
            managerId: String(user.id)
          }
        })
      );

      const clients = clientsResponse.map((account) => {
        const clientJson = {
          tipo: 'CLIENTE',
          usuario: {
            id: Number(account.cliente),
            cpf: account.cliente,
            name: `Cliente ${account.cliente}`,
            phone: '',
            email: '',
          },
          saldo: account.saldo ?? 0,
          limite: account.limite ?? 0,
          salario: 0,
          cidade: '',
          estado: '',
          telefone: '',
          numero_conta: account.numero ?? '',
          gerente: account.gerente,
          gerente_nome: '',
        };

        return Client.fromJson(clientJson);
      });
      clients.sort((a, b) => b.balance - a.balance);
      this.clients.set(clients);
    } catch (error) {
      this.errorHandler.handleError(error as Error);
      this.clients.set([]);
    }
  }

}
