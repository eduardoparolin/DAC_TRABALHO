import {inject, Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmationDialogComponent} from '../utils/confirmation-dialog/confirmation-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {lastValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DepositService {
  dialog = inject(MatDialog);
  private _snackBar = inject(MatSnackBar);

  constructor() { }

  async deposit(amount: number): Promise<boolean> {
    const ref = this.dialog.open(ConfirmationDialogComponent);
    const afterClosed = await lastValueFrom(ref.afterClosed());
    if (afterClosed) {
      await this.processDeposit(amount);
      return true;
    }
    return false;
  }

  private processDeposit(amount: number): void {
    this._snackBar.open(`Depósito confirmado: ${amount}`, "Ok");
  }
}
