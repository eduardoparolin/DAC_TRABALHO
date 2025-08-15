import {inject, Injectable} from '@angular/core';
import {HttpClientMockService} from '../utils/http-client-mock.service';
import {environment} from '../../environments/environment';
import {lastValueFrom} from 'rxjs';
import {ClientResponse} from './clients.types';

@Injectable({
  providedIn: 'root'
})
export class ClientsService {

  http = inject(HttpClientMockService);
  constructor() { }

  getClients(filtros: 'para_aprovar' | 'adm_relatorio_clientes' | 'melhores_clientes') {
    return lastValueFrom(this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`));
  }

  getClientById(id: string) {
    return lastValueFrom(this.http.get<ClientResponse>(`${environment.baseUrl}/clientes/${id}`));
  }
}
