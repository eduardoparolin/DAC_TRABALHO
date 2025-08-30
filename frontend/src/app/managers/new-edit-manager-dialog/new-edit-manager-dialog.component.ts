import {Component, inject} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-new-edit-manager-dialog',
  imports: [
    FormsModule,
    MatError,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    MatButton
  ],
  templateUrl: './new-edit-manager-dialog.component.html',
  styleUrl: './new-edit-manager-dialog.component.scss'
})
export class NewEditManagerDialogComponent {
  dialog = inject(MatDialogRef)
  cpfFormControl = new FormControl(null, [Validators.required]);
  nameFormControl = new FormControl(null, [Validators.required]);
  emailFormControl = new FormControl(null, [Validators.required, Validators.email]);
  passwordFormControl = new FormControl(null, [Validators.required]);

  cancel() {
    this.dialog.close();
  }

  save() {
    if (this.cpfFormControl.valid && this.nameFormControl.valid && this.emailFormControl.valid && this.passwordFormControl.valid) {
      // Save logic here
      this.dialog.close();
    }
  }
}
