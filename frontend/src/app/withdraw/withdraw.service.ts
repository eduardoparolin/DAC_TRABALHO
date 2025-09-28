import {inject, Injectable} from '@angular/core';
import {ConfirmationDialogComponent} from '../utils/confirmation-dialog/confirmation-dialog.component';
import {lastValueFrom} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class WithdrawService {
  dialog = inject(MatDialog);
  private _snackBar = inject(MatSnackBar);

  constructor() { }

  async withdraw(amount: number): Promise<boolean> {
    const ref = this.dialog.open(ConfirmationDialogComponent);
    const afterClosed = await lastValueFrom(ref.afterClosed());
    if (afterClosed) {
      await this.processWithdraw(amount);
      return true;
    }
    return false;
  }

  private processWithdraw(amount: number): void {
    this._snackBar.open(`Saque confirmado: ${amount}`, "Ok");
  }
}
