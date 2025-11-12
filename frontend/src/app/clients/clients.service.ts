import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {lastValueFrom} from 'rxjs';
import {ClientResponse} from './clients.types';
import {Client} from './client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientsService {
  clients = signal<Client[]>([]);
  filteredClients = signal<Client[]>([]);
  http = inject(HttpClient);
  constructor() { }

  getClients(filtros: 'para_aprovar' | 'adm_relatorio_clientes' | 'melhores_clientes') {
    return lastValueFrom(this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`));
  }

  getClientById(id: string) {
    return lastValueFrom(this.http.get<ClientResponse>(`${environment.baseUrl}/clientes/${id}`));
  }

  getClientByCpf(cpf: string) {
    return lastValueFrom(this.http.get<ClientResponse>(`${environment.baseUrl}/client/${cpf}`));
  }

  async getAllClients() {
    const clientsResponse = await lastValueFrom(this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`));
    this.clients.set(clientsResponse.map(client => Client.fromJson(client)));
    this.filteredClients.set(this.clients());
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
}
