import {inject, Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ConfirmationDialogComponent} from '../utils/confirmation-dialog/confirmation-dialog.component';
import {lastValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  dialog = inject(MatDialog);
  private _snackBar = inject(MatSnackBar);

  constructor() { }

  async transfer(amount: number, accountNumber: string): Promise<boolean> {
    const ref = this.dialog.open(ConfirmationDialogComponent);
    const afterClosed = await lastValueFrom(ref.afterClosed());
    if (afterClosed) {
      await this.processTransfer(amount, accountNumber);
      return true;
    }
    return false;
  }

  private processTransfer(amount: number, accountNumber: string): void {
    this._snackBar.open(`Transferência confirmada: ${amount} para conta ${accountNumber}`, "Ok");
  }
}
