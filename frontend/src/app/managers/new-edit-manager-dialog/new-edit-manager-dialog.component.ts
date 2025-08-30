import {Component, inject, OnInit} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Manager} from '../manager.model';

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
export class NewEditManagerDialogComponent implements OnInit {
  dialog = inject(MatDialogRef)
  dialogData = inject<{manager: Manager | null}>(MAT_DIALOG_DATA)
  cpfFormControl = new FormControl<string | null>(null, [Validators.required]);
  nameFormControl = new FormControl<string | null>(null, [Validators.required]);
  emailFormControl = new FormControl<string | null>(null, [Validators.required, Validators.email]);
  passwordFormControl = new FormControl<string | null>(null);

  ngOnInit() {
    if (this.dialogData.manager != null) {
      this.cpfFormControl.setValue(this.dialogData.manager.cpf);
      this.cpfFormControl.disable()
      this.nameFormControl.setValue(this.dialogData.manager.name);
      this.emailFormControl.setValue(this.dialogData.manager.email);
      this.passwordFormControl.removeValidators([Validators.required])
    } else {
      this.passwordFormControl.addValidators([Validators.required]);
    }
  }

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
