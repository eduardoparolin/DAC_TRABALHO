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
export class TransferService {
  dialog = inject(MatDialog);
  private http = inject(HttpClient);
  private accountService = inject(ClientAccountService);
  private errorHandler = inject(ErrorHandlerService);
  loading = signal(false);

  constructor() { }

  async transfer(amount: number, accountNumber: string): Promise<boolean> {
    const ref = this.dialog.open(ConfirmationDialogComponent);
    const afterClosed = await lastValueFrom(ref.afterClosed());
    if (!afterClosed) {
      return false;
    }
    return this.processTransfer(amount, accountNumber);
  }

  private async processTransfer(amount: number, destinationAccount: string): Promise<boolean> {
    this.loading.set(true);
    try {
      const sourceAccount = await this.accountService.getAccountNumber();
      const body = {
        valor: amount,
        destino: destinationAccount.replace(/\D/g, ''),
      };
      await lastValueFrom(
        this.http.post(
          `${environment.baseUrl}/contas/${sourceAccount}/transferir`,
          body
        )
      );
      await this.accountService.refresh();
      this.errorHandler.handleSuccess('Transferência realizada com sucesso!');
      return true;
    } catch (error) {
      this.errorHandler.handleError(error as Error);
      return false;
    } finally {
      this.loading.set(false);
    }
  }
}
