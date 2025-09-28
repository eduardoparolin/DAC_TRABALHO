import { Component, inject, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { ManagersService } from './managers.service';
import { MatIconModule } from '@angular/material/icon';
import { ConfirmationDialogComponent } from '../utils/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { NewEditManagerDialogComponent } from './new-edit-manager-dialog/new-edit-manager-dialog.component';
import { Manager } from './manager.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-managers',
  imports: [MatTableModule, MatIconModule, MatButton],
  templateUrl: './managers.component.html',
  styleUrl: './managers.component.scss',
})
export class ManagersComponent implements OnInit {
  service = inject(ManagersService);
  private _snackBar = inject(MatSnackBar);

  readonly dialog = inject(MatDialog);
  displayedColumns: string[] = ['NAME', 'CPF', 'EMAIL', 'PHONE', 'ACTIONS'];

  ngOnInit() {
    this.service.getAllManagers();
  }

  deleteManager(id: number) {
    this._snackBar.open('Gerente removido com sucesso');
    this.dialog.open(ConfirmationDialogComponent, { width: '250px' });
  }

  newManager() {
    this.dialog.open(NewEditManagerDialogComponent, {
      data: {
        manager: null,
      },
    });
  }

  editManager(manager: Manager) {
    this.dialog.open(NewEditManagerDialogComponent, {
      data: {
        manager: manager,
      },
    });
  }
}
