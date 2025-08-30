import {inject, Injectable, signal} from '@angular/core';
import {Client} from '../clients/client.model';
import {lastValueFrom} from 'rxjs';
import {ClientResponse} from '../clients/clients.types';
import {environment} from '../../environments/environment';
import {HttpClientMockService} from '../utils/http-client-mock.service';

@Injectable({
  providedIn: 'root'
})
export class ClientApprovalService {
  http = inject(HttpClientMockService);
  clients = signal<Client[]>([]);
  constructor() { }

  async getAllClients() {
    const clientsResponse = await lastValueFrom(this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`));
    this.clients.set(clientsResponse.map(client => Client.fromJson(client)));
  }

  approveClient(clientId: string) {

  }

  rejectClient(clientId: string) {

  }
}
