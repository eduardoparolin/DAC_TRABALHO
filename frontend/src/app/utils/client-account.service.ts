import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { SessionService } from '../session/session.service';
import { lastValueFrom } from 'rxjs';

export interface AccountInfo {
  cliente: string;
  numero: string;
  saldo: number;
  limite: number;
  gerente: string;
  criacao: string;
  status: string;
}

@Injectable({
  providedIn: 'root',
})
export class ClientAccountService {
  private http = inject(HttpClient);
  private session = inject(SessionService);
  account = signal<AccountInfo | null>(null);
  loading = signal(false);

  async loadAccount(force = false): Promise<AccountInfo> {
    if (this.account() && !force) {
      return this.account()!;
    }

    const user = this.session.getUser();
    if (!user) {
      throw new Error('Usuário não autenticado.');
    }

    this.loading.set(true);
    try {
      const account = await lastValueFrom(
        this.http.get<AccountInfo>(
          `${environment.baseUrl}/contas/cliente/${user.id}`
        )
      );
      this.account.set(account);
      console.log(this.account());

      return account;
    } finally {
      this.loading.set(false);
    }
  }

  async getAccountNumber(): Promise<string> {
    const account = await this.loadAccount();
    if (!account.numero) {
      throw new Error('Conta não encontrada para o usuário.');
    }
    return account.numero;
  }

  async refresh(): Promise<void> {
    await this.loadAccount(true);
  }
}
