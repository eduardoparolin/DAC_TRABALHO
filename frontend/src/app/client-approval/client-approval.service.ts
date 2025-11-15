import { inject, Injectable, signal } from '@angular/core';
import { Client } from '../clients/client.model';
import { lastValueFrom } from 'rxjs';
import { ClientResponse } from '../clients/clients.types';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../utils/confirmation-dialog/confirmation-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import {RejectClientDialogComponent} from './components/reject-client-dialog/reject-client-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class ClientApprovalService {
  http = inject(HttpClient);
  dialog = inject(MatDialog);
  clients = signal<Client[]>([]);
  private _snackBar = inject(MatSnackBar);

  constructor() {
  }

  async getAllClients() {
    const clientsResponse = await lastValueFrom(
      this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`)
    );
    this.clients.set(clientsResponse.map((client) => {
      const clientJson = {
        tipo: client.tipo,
        usuario: {
          id: client.id,
          cpf: client.cpf,
          name: client.nome,
          email: client.email,
        },
        saldo: client.saldo,
        limite: client.limite,
        salario: client.salario,
        endereco: client.endereco,
        cidade: client.cidade,
        estado: client.estado,
        telefone: client.telefone,
        numero_conta: client.numero_conta,
        gerente: client.gerente,
        gerente_nome: client.gerente_nome,
      };

      return Client.fromJson(clientJson);
    }));
  }

  approveClient(clientId: string) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {});

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._snackBar.open('Cliente aprovado com sucesso!');
      }
    });
  }

  rejectClient(clientId: string) {
    this.dialog.open(RejectClientDialogComponent, {
      data: {
        id: clientId
      }
    });
  }
}
