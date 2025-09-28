import { inject, Injectable, signal } from '@angular/core';
import { Client } from '../clients/client.model';
import { lastValueFrom } from 'rxjs';
import { ClientResponse } from '../clients/clients.types';
import { environment } from '../../environments/environment';
import { HttpClientMockService } from '../utils/http-client-mock.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../utils/confirmation-dialog/confirmation-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class ClientApprovalService {
  http = inject(HttpClientMockService);
  dialog = inject(MatDialog);
  clients = signal<Client[]>([]);
  private _snackBar = inject(MatSnackBar);

  constructor() {}

  async getAllClients() {
    const clientsResponse = await lastValueFrom(
      this.http.get<ClientResponse[]>(`${environment.baseUrl}/clientes`)
    );
    this.clients.set(clientsResponse.map((client) => Client.fromJson(client)));
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
    this.dialog.open(ConfirmationDialogComponent, {});
  }
}
