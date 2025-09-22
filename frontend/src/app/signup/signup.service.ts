import {inject, Injectable, signal} from '@angular/core';
import {lastValueFrom} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Address} from './address';

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

@Injectable({
  providedIn: 'root'
})
export class SignupService {
  loading = signal(false);
  cepLoading = signal(false);
  http = inject(HttpClient);
  constructor() { }


  async fetchAddress(cep: string): Promise<Address | undefined> {
    let address;
    this.cepLoading.set(true);
    if (cep && cep.trim() !== '') {
      const data = await lastValueFrom(
        this.http.get<ViaCepResponse | null>(
          `https://viacep.com.br/ws/${cep}/json/`
        )
      ).catch(() => {
        alert('Erro ao buscar o CEP.');
        return null;
      });
      if (data) {
        address = Address.fromViaCepResponse(data);
      } else {
        alert('CEP não encontrado.');
      }
      this.cepLoading.set(false);
    }
    return address;
  }
}
