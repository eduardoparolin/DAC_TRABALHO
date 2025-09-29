import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Client } from './client.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-client-details-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, CommonModule],
  template: `
    <h1 mat-dialog-title>Detalhes do Cliente</h1>
    <div mat-dialog-content>
      <div class="client-details">
        <div class="detail-row"><strong>CPF:</strong> {{ data.cpf }}</div>
        <div class="detail-row"><strong>Nome:</strong> {{ data.name }}</div>
        <div class="detail-row"><strong>E-mail:</strong> {{ data.email }}</div>
        <div class="detail-row">
          <strong>Telefone:</strong> {{ data.phone }}
        </div>
        <div class="detail-row">
          <strong>Número da Conta:</strong> {{ data.accountNumber }}
        </div>
        <div class="detail-row"><strong>Cidade:</strong> {{ data.city }}</div>
        <div class="detail-row"><strong>Estado:</strong> {{ data.state }}</div>
        <div class="detail-row">
          <strong>Salário:</strong>
          {{ data.wage | currency : 'BRL' : 'symbol' : '1.2-2' }}
        </div>
        <div class="detail-row">
          <strong>Saldo:</strong>
          {{ data.balance | currency : 'BRL' : 'symbol' : '1.2-2' }}
        </div>
        <div class="detail-row">
          <strong>Limite:</strong>
          {{ data.limit | currency : 'BRL' : 'symbol' : '1.2-2' }}
        </div>
        <div class="detail-row">
          <strong>CPF do Gerente:</strong> {{ data.cpfManager }}
        </div>
        <div class="detail-row">
          <strong>Nome do Gerente:</strong> {{ data.nameManager }}
        </div>
      </div>
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="onClose()">Fechar</button>
    </div>
  `,
  styles: [
    `
      .client-details {
        min-width: 400px;
        padding: 16px 0;
      }
      .detail-row {
        margin-bottom: 12px;
        padding: 8px 0;
        border-bottom: 1px solid #e0e0e0;
      }
      .detail-row:last-child {
        border-bottom: none;
      }
    `,
  ],
})
export class ClientDetailsDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ClientDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Client
  ) {}

  onClose(): void {
    this.dialogRef.close();
  }
}
