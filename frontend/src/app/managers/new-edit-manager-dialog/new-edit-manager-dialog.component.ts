import {Component, inject, OnInit} from '@angular/core';
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
  MatLabel, MatSuffix,
} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Manager} from '../manager.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ManagersService} from '../managers.service';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {NgxMaskDirective} from 'ngx-mask';
import {insertManagerRequest} from '../manager.types';

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
    MatSuffix,
    MatIcon,
    MatTooltip,
    NgxMaskDirective,
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

  ngOnInit() {
    if (this.dialogData.manager != null) {
      this.cpfFormControl.setValue(this.dialogData.manager.cpf);
      this.cpfFormControl.disable();
      this.nameFormControl.setValue(this.dialogData.manager.name);
      this.emailFormControl.setValue(this.dialogData.manager.email);
      this.passwordFormControl.removeValidators([Validators.required]);
      console.log('Tipo do manager:', this.dialogData.manager.type);
    } else {
      this.passwordFormControl.addValidators([Validators.required]);
    }
  }

  cancel() {
    this.dialog.close();
  }

  async save() {
    if (this.dialogData.manager == null) {//new
      if (
        this.cpfFormControl.valid &&
        this.nameFormControl.valid &&
        this.emailFormControl.valid &&
        this.passwordFormControl.valid
      ) {
        const insertManagerRequest: insertManagerRequest = {
          nome: this.nameFormControl.value!,
          email: this.emailFormControl.value!,
          senha: this.passwordFormControl.value!,
          tipo: 'GERENTE',
          cpf: this.cpfFormControl.value!,
        };

        const response = await this.managerService.insert(insertManagerRequest);
        this.dialog.close(response);
        return;
      }
    } else {//edit
      if (
        this.nameFormControl.valid &&
        this.emailFormControl.valid
      ) {
        const updateManagerRequest: any = {
          nome: this.nameFormControl.value!,
          email: this.emailFormControl.value!,
          tipo: 'GERENTE',
        };

        // Só adiciona senha se tiver valor
        if (this.passwordFormControl.value) {
          updateManagerRequest.senha = this.passwordFormControl.value;
        }

        await this.managerService.update(
          this.cpfFormControl.value!,
          updateManagerRequest
        );
        this.dialog.close(true);
      }
    }

  }
}
