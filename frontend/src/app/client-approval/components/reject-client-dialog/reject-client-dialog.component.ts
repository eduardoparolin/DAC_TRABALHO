import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
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
@Inject(MAT_DIALOG_DATA) public data: RejectClientDialogData
) {}

onCancel(): void {
  this.dialogRef.close(null);
}

onConfirm(): void {
  const reason = this.whyRejected.trim();
  if (!reason) {
    return;
  }

  this.dialogRef.close(reason);
}
}
