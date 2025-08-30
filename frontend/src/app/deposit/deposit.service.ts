import {inject, Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmationDialogComponent} from '../utils/confirmation-dialog/confirmation-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class DepositService {
  dialog = inject(MatDialog);

  constructor() { }

  deposit(amount: number): void {
    const ref = this.dialog.open(ConfirmationDialogComponent);
    ref.afterClosed().subscribe(result => {
      if (result === 'confirm') {
        this.processDeposit(amount);
      }
    });
  }

  private processDeposit(amount: number): void {
    console.log(`Deposited amount: ${amount}`);
  }
}
