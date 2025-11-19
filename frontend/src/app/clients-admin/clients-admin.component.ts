import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { SessionService } from '../session/session.service';
import {ClientsService} from '../clients/clients.service';

@Component({
  selector: 'app-clients-admin',
  imports: [
    MatTableModule,
    MatIconModule,
    MatButton,
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
  ],
  templateUrl: './clients-admin.component.html',
  styleUrl: './clients-admin.component.scss',
})
export class ClientsAdminComponent {
  service = inject(ClientsService);
  readonly dialog = inject(MatDialog);
  displayedColumns: string[] = [
    'CPF',
    'NAME',
    'EMAIL',
    'WAGE',
    'ACCOUNT_NUMBER',
    'BALANCE',
    'LIMIT',
    'CPF_GERENTE',
    'NOME_GERENTE',
  ];
  filterControl = new FormControl();
  sessionService = inject(SessionService);

  ngOnInit() {
    this.service.getAllClients(true);
    if (this.sessionService.user()?.isManager()) {
      this.displayedColumns.push('ACTIONS');
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.service.filterClients(filterValue);
  }

  clearFilter() {
    this.filterControl.setValue('');
    this.service.clearFilter();
  }
}
