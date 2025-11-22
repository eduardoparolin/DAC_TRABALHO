import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {environment} from '../../environments/environment';
import {ErrorHandlerService} from '../utils/error-handler.service';
import {SessionService} from '../session/session.service';
import {idText} from 'typescript';

export interface ClientProfile {
  cliente: {
    cpf: string;
    nome: string;
    email: string;
    telefone: string;
    salario: number;
    endereco: {
      rua: string;
      numero: string;
      complemento: string;
      cidade: string;
      estado: string;
      CEP: string;
    };
  };
  conta: {
    numero: string;
    saldo: number;
    limite: number;
    status: string;
    criacao: string;
  } | null;
  gerente: {
    cpf: string;
    nome: string;
    email: string;
    tipo: string;
  } | null;
}

@Injectable({
  providedIn: 'root',
})
export class EditProfileService {
  http = inject(HttpClient);
  session = inject(SessionService);
  errorHandler = inject(ErrorHandlerService);
  loading = signal(false);

  async getClientProfile(): Promise<ClientProfile | null> {
    this.loading.set(true);
    const user = this.session.getUser();

    if (!user?.cpf) {
      this.loading.set(false);
      this.errorHandler.handleError(new Error('CPF do usuário não encontrado'), {showError: true});
      return null;
    }

    try {
      const response = await lastValueFrom(
        this.http.get<ClientProfile>(`${environment.baseUrl}/clientes/perfil/${user.cpf}`)
      );
      this.loading.set(false);
      return response;
    } catch (error) {
      this.loading.set(false);
      this.errorHandler.handleError(error as Error, {showError: true});
      return null;
    }
  }

  async editProfile(cpf: string, name: string, email: string, phone: string, endereco: string, cidade: string, estado: string, cep: string, salary: number): Promise<void> {
    this.loading.set(true);
    const user = this.session.getUser();
    try {
      const response = await lastValueFrom(
        this.http.put(`${environment.baseUrl}/clientes/${cpf}`, {
          cpf,
          nome: name,
          email,
          telefone: phone,
          salario: salary,
          endereco,
          cidade,
          estado,
          CEP: cep
        })
      );
      this.loading.set(false);
      this.errorHandler.handleSuccess('Perfil atualizado com sucesso!');
    } catch (error) {
      this.loading.set(false);
      this.errorHandler.handleError(error as Error, {showError: true});
    }
  }
}
