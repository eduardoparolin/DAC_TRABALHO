import {Component, inject, OnInit} from '@angular/core';
import {
  MatTableModule
} from '@angular/material/table';
import {ManagersService} from './managers.service';
import {MatIconModule} from '@angular/material/icon';
import {ConfirmationDialogComponent} from '../utils/confirmation-dialog/confirmation-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-managers',
  imports: [MatTableModule, MatIconModule],
  templateUrl: './managers.component.html',
  styleUrl: './managers.component.scss'
})
export class ManagersComponent implements OnInit {
    service = inject(ManagersService);
    readonly dialog = inject(MatDialog);
    displayedColumns: string[] = ['NAME', 'CPF', 'EMAIL', 'PHONE', 'ACTIONS'];

    ngOnInit() {
      this.service.getAllManagers();
    }

    deleteManager(id: number) {
      this.dialog.open(ConfirmationDialogComponent, {width: '250px'});
    }
}
