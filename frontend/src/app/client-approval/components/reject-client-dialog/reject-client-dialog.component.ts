import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormsModule} from '@angular/forms';

export interface RejectClientDialogData {
  clientId?: number;
  clientName?: string;
}

@Component({
  selector: 'app-reject-client-dialog',
  imports: [
    MatFormField,
    MatLabel,
    MatButton,
    MatInput,
    FormsModule,
  ],
  templateUrl: './reject-client-dialog.component.html',
  styleUrl: './reject-client-dialog.component.scss'
})
export class RejectClientDialogComponent {
  whyRejected: string = '';

constructor(
  private dialogRef: MatDialogRef<RejectClientDialogComponent>,
  private snackBar: MatSnackBar,
@Inject(MAT_DIALOG_DATA) public data: RejectClientDialogData
) {}

onCancel(): void {
  this.dialogRef.close(null);
}

onConfirm(): void {
  if (!this.whyRejected) {
  this.snackBar.open('Selecione um motivo!', 'Fechar', {
    duration: 3000,
    panelClass: ['error-snackbar']
  });
  return;
}

const result = this.whyRejected;

this.snackBar.open('Cliente recusado com sucesso!', 'OK', {
  duration: 3000,
  horizontalPosition: 'center',
  verticalPosition: 'top'
});

this.dialogRef.close(result);
}
}
