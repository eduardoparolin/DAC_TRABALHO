import { Component, inject, OnInit } from '@angular/core';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MatError,
  MatFormField,
  MatInput,
  MatLabel,
} from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Manager } from '../manager.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ManagersService } from '../managers.service';
import { MatOption, MatSelect } from '@angular/material/select';

@Component({
  selector: 'app-new-edit-manager-dialog',
  imports: [
    FormsModule,
    MatError,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    MatButton,
    MatSelect,
    MatOption,
  ],
  templateUrl: './new-edit-manager-dialog.component.html',
  styleUrl: './new-edit-manager-dialog.component.scss',
})
export class NewEditManagerDialogComponent implements OnInit {
  managerService = inject(ManagersService);
  dialog = inject(MatDialogRef);
  dialogData = inject<{ manager: Manager | null }>(MAT_DIALOG_DATA);
  private _snackBar = inject(MatSnackBar);
  cpfFormControl = new FormControl<string | null>(null, [Validators.required]);
  nameFormControl = new FormControl<string | null>(null, [Validators.required]);
  emailFormControl = new FormControl<string | null>(null, [
    Validators.required,
    Validators.email,
  ]);
  passwordFormControl = new FormControl<string | null>(null);
  tipoFormControl = new FormControl<string | null>(null, [Validators.required]);

  ngOnInit() {
    if (this.dialogData.manager != null) {
      this.cpfFormControl.setValue(this.dialogData.manager.cpf);
      this.cpfFormControl.disable();
      this.nameFormControl.setValue(this.dialogData.manager.name);
      this.emailFormControl.setValue(this.dialogData.manager.email);
      this.passwordFormControl.removeValidators([Validators.required]);
      console.log('Tipo do manager:', this.dialogData.manager.type);

      setTimeout(() => {
        this.tipoFormControl.setValue(this.dialogData.manager!.type);
      }, 0);

    } else {
      this.passwordFormControl.addValidators([Validators.required]);
    }
  }

  cancel() {
    this.dialog.close();
  }

  async save() {
  if (
    this.nameFormControl.valid &&
    this.emailFormControl.valid &&
    this.tipoFormControl.valid
  ) {
    const updateManagerRequest: any = {
      nome: this.nameFormControl.value!,
      email: this.emailFormControl.value!,
      tipo: this.tipoFormControl.value!,
    };

    // Só adiciona senha se tiver valor
    if (this.passwordFormControl.value) {
      updateManagerRequest.senha = this.passwordFormControl.value;
    }

    await this.managerService.update(
      this.cpfFormControl.value!,
      updateManagerRequest
    );
  }
}
}
