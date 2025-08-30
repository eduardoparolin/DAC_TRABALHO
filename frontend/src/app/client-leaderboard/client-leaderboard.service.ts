import {inject, Injectable, signal} from '@angular/core';
import {HttpClientMockService} from '../utils/http-client-mock.service';
import {MatDialog} from '@angular/material/dialog';
import {Client} from '../clients/client.model';
import {lastValueFrom} from 'rxjs';
import {ClientResponse} from '../clients/clients.types';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClientLeaderboardService {

  http = inject(HttpClientMockService);
  dialog = inject(MatDialog);
  clients = signal<Client[]>([]);
  constructor() { }

  async getAllClients() {
    const clientsResponse = await lastValueFrom(this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`));
    const clients = clientsResponse.map(client => Client.fromJson(client));
    clients.sort((a, b) => b.balance - a.balance);
    this.clients.set(clients);
  }

}
