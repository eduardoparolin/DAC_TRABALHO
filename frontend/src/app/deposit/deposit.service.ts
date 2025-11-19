import {inject, Injectable, signal} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmationDialogComponent} from '../utils/confirmation-dialog/confirmation-dialog.component';
import {lastValueFrom} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {ClientAccountService} from '../utils/client-account.service';
import {ErrorHandlerService} from '../utils/error-handler.service';

@Injectable({
  providedIn: 'root'
})
export class DepositService {
  dialog = inject(MatDialog);
  private http = inject(HttpClient);
  private accountService = inject(ClientAccountService);
  private errorHandler = inject(ErrorHandlerService);
  loading = signal(false);

  constructor() { }

  async deposit(amount: number): Promise<boolean> {
    const ref = this.dialog.open(ConfirmationDialogComponent);
    const afterClosed = await lastValueFrom(ref.afterClosed());
    if (!afterClosed) {
      return false;
    }
    return this.processDeposit(amount);
  }

  private async processDeposit(amount: number): Promise<boolean> {
    this.loading.set(true);
    try {
      const accountNumber = await this.accountService.getAccountNumber();
      await lastValueFrom(
        this.http.post(
          `${environment.baseUrl}/contas/${accountNumber}/depositar`,
          { valor: amount }
        )
      );
      await this.accountService.refresh();
      this.errorHandler.handleSuccess('Depósito realizado com sucesso!');
      return true;
    } catch (error) {
      this.errorHandler.handleError(error as Error);
      return false;
    } finally {
      this.loading.set(false);
    }
  }
}
