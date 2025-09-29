import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { ClientsService } from './clients.service';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { SessionService } from '../session/session.service';
import { ClientDetailsDialogComponent } from './client-details-dialog.component';
import { Client } from './client.model';

@Component({
  selector: 'app-clients',
  imports: [
    MatTableModule,
    MatIconModule,
    MatButton,
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
  ],
  templateUrl: './clients.component.html',
  styleUrl: './clients.component.scss',
})
export class ClientsComponent {
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
  ];
  filterControl = new FormControl();
  sessionService = inject(SessionService);

  ngOnInit() {
    this.service.getAllClients();
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

  openClientDetails(client: Client): void {
    this.dialog.open(ClientDetailsDialogComponent, {
      width: '500px',
      data: client,
    });
  }
}
