import { inject, Injectable, signal } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../utils/confirmation-dialog/confirmation-dialog.component';
import {RejectClientDialogComponent} from './components/reject-client-dialog/reject-client-dialog.component';
import {ErrorHandlerService} from '../utils/error-handler.service';
import { SessionService } from '../session/session.service';

export interface PendingClient {
  cpf: string;
  name: string;
  email: string;
  wage: number;
}

type PendingClientApiResponse = {
  cpf: string;
  nome: string;
  email: string;
  salario: number;
  contaId?: number;
  gerenteId?: number;
};

@Injectable({
  providedIn: 'root',
})
export class ClientApprovalService {
  http = inject(HttpClient);
  dialog = inject(MatDialog);
  errorHandler = inject(ErrorHandlerService);
  clients = signal<PendingClient[]>([]);
  loading = signal(false);
  session = inject(SessionService);

  constructor() {
  }

  async getAllClients() {
    this.loading.set(true);
    try {

      const user = this.session.user();
      const clientsResponse = await lastValueFrom(
        this.http.get<PendingClientApiResponse[]>(
          `${environment.baseUrl}/clientes`,
          {
            params: { filtro: 'para_aprovar' },
          }
        )
      );

      const  filtered = clientsResponse.filter(
        client => client.gerenteId === user?.cpf
      )
      this.clients.set(
        filtered.map((client) => ({
          cpf: client.cpf,
          name: client.nome,
          email: client.email,
          wage: client.salario,
        }))
      );
    } catch (error) {
      this.errorHandler.handleError(error as Error);
    } finally {
      this.loading.set(false);
    }
  }

  approveClient(client: PendingClient) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {});

    dialogRef.afterClosed().subscribe(async (result) => {
      if (result) {
        await this.executeApprove(client);
      }
    });
  }

  rejectClient(client: PendingClient) {
    const dialogRef = this.dialog.open(RejectClientDialogComponent, {
      data: {
        clientName: client.name
      },
    });

    dialogRef.afterClosed().subscribe(async (reason?: string) => {
      if (!reason) {
        return;
      }

      await this.executeReject(client, reason);
    });
  }

  private async executeApprove(client: PendingClient) {
    this.loading.set(true);
    try {
      await lastValueFrom(
        this.http.post(
          `${environment.baseUrl}/clientes/${client.cpf}/aprovar`,
          {}
        )
      );

      this.clients.update((current) =>
        current.filter((item) => item.cpf !== client.cpf)
      );

      this.errorHandler.handleSuccess('Cliente aprovado com sucesso!');
    } catch (error) {
      this.errorHandler.handleError(error as Error);
    } finally {
      this.loading.set(false);
    }
  }

  private async executeReject(client: PendingClient, reason: string) {
    this.loading.set(true);
    try {
      await lastValueFrom(
        this.http.post(
          `${environment.baseUrl}/clientes/${client.cpf}/rejeitar`,
          {
            usuario: {
              cpf: client.cpf,
              email: client.email,
              nome: client.name,
            },
            motivo: reason,
          }
        )
      );

      this.clients.update((current) =>
        current.filter((item) => item.cpf !== client.cpf)
      );
      this.errorHandler.handleSuccess('Cliente rejeitado com sucesso!');
    } catch (error) {
      this.errorHandler.handleError(error as Error);
    } finally {
      this.loading.set(false);
    }
  }
}
