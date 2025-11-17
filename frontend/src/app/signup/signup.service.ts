import { inject, Injectable, signal } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Address } from './address';
import { environment } from '../../environments/environment';
import { ErrorHandlerService } from '../utils/error-handler.service';

export type ViaCepResponse = {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  ibge: string;
  gia: string;
  ddd: string;
  siafi: string;
};

export interface SignupData {
  cpf: string;
  nome: string;
  email: string;
  telefone: string;
  salario: number;
  endereco: string;
  complemento?: string;
  CEP: string;
  cidade: string;
  estado: string;
}

@Injectable({
  providedIn: 'root',
})
export class SignupService {
  loading = signal(false);
  cepLoading = signal(false);
  http = inject(HttpClient);
  errorHandler = inject(ErrorHandlerService);
  constructor() {}

  async fetchAddress(cep: string): Promise<Address | undefined> {
    let address;
    this.cepLoading.set(true);
    if (cep && cep.trim() !== '') {
      const data = await lastValueFrom(
        this.http.get<ViaCepResponse | null>(
          `https://viacep.com.br/ws/${cep}/json/`
        )
      ).catch(() => {
        this.errorHandler.handleError('Erro ao buscar o CEP.');
        return null;
      });
      if (data) {
        address = Address.fromViaCepResponse(data);
      } else {
        this.errorHandler.handleWarning('CEP não encontrado.');
      }
      this.cepLoading.set(false);
    }
    return address;
  }

  async signup(data: SignupData): Promise<void> {
    this.loading.set(true);
    try {
      await lastValueFrom(
        this.http.post(`${environment.baseUrl}/clientes`, data)
      );
    } finally {
      this.loading.set(false);
    }
  }
}
