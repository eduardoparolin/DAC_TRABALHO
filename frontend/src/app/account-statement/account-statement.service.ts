import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { ClientAccountService } from '../utils/client-account.service';
import { ErrorHandlerService } from '../utils/error-handler.service';

export interface Transaction {
  data: string;
  tipo: string;
  origem: string;
  destino: string | null;
  valor: number;
}

export interface StatementResponse {
  conta: string;
  saldo: number;
  movimentacoes: Transaction[];
}

@Injectable({
  providedIn: 'root'
})
export class AccountStatementService {
  private http = inject(HttpClient);
  private accountService = inject(ClientAccountService);
  private errorHandler = inject(ErrorHandlerService);
  statement = signal<StatementResponse | null>(null);
  loading = signal(false);

  constructor() { }

  async loadStatement(): Promise<void> {
    this.loading.set(true);
    try {
      const accountNumber = await this.accountService.getAccountNumber();
      const response = await lastValueFrom(
        this.http.post<StatementResponse>(
          `${environment.baseUrl}/contas/${accountNumber}/extrato`,
          {}
        )
      );
      this.statement.set(response);
    } catch (error) {
      this.statement.set(null);
      this.errorHandler.handleError(error as Error);
    } finally {
      this.loading.set(false);
    }
  }
}
