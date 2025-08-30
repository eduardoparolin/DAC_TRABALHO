import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MatTableModule
} from "@angular/material/table";
import {MatIconModule} from '@angular/material/icon';
import {MatDialog} from '@angular/material/dialog';
import {ClientsService} from './clients.service';

@Component({
  selector: 'app-clients',
  imports: [MatTableModule, MatIconModule, MatButton],
  templateUrl: './clients.component.html',
  styleUrl: './clients.component.scss'
})
export class ClientsComponent {
  service = inject(ClientsService);
  readonly dialog = inject(MatDialog);
  displayedColumns: string[] = ['CPF', 'NAME', 'EMAIL', 'WAGE', 'ACCOUNT_NUMBER', 'BALANCE', 'LIMIT', 'CPF_MANAGER', 'NAME_MANAGER'];

  ngOnInit() {
    this.service.getAllClients();
  }
}
