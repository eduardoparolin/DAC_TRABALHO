import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MatDialog} from '@angular/material/dialog';
import {Client} from '../clients/client.model';
import {lastValueFrom} from 'rxjs';
import {ClientResponse} from '../clients/clients.types';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClientLeaderboardService {

  http = inject(HttpClient);
  dialog = inject(MatDialog);
  clients = signal<Client[]>([]);
  constructor() { }

  async getAllClients() {
    const clientsResponse = await lastValueFrom(this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`));
    const clients = clientsResponse.map(client => {
      const clientJson = {
        tipo: client.tipo,
        usuario: {
          id: client.id,
          cpf: client.cpf,
          name: client.nome,
          email: client.email,
        },
        saldo: client.saldo,
        limite: client.limite,
        salario: client.salario,
        endereco: client.endereco,
        cidade: client.cidade,
        estado: client.estado,
        telefone: client.telefone,
        numero_conta: client.numero_conta,
        gerente: client.gerente,
        gerente_nome: client.gerente_nome,
      };

      return Client.fromJson(clientJson);
    });
    clients.sort((a, b) => b.balance - a.balance);
    this.clients.set(clients);
  }

}
