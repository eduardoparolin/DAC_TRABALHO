import {Component, inject, signal} from '@angular/core';
import {SessionService} from '../session/session.service';
import {MatButton} from '@angular/material/button';
import {CurrencyPipe} from '@angular/common';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import { EditProfileService } from './edit-profile.service';
import {NgxMaskDirective} from 'ngx-mask';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {CustomValidators} from '../utils/validators';
import {SignupService} from '../signup/signup.service';

@Component({
  selector: 'app-edit-profile',
  imports: [
    MatButton,
    CurrencyPipe,
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatFormField,
    ReactiveFormsModule,
    NgxMaskDirective,
    MatError,
    MatProgressSpinner,
    MatSuffix
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.scss'
})
export class EditProfileComponent {
  sessionService = inject(SessionService);
    editProfileService = inject(EditProfileService);
    editing = signal<boolean>(false);

    nameControl = new FormControl('', [Validators.required]);
    emailControl = new FormControl('', [Validators.required, Validators.email]);
    phoneControl = new FormControl('', [Validators.required]);
    salaryControl = new FormControl('', [Validators.required]);

  ruaFormControl = new FormControl('', [Validators.required]);
  bairroFormControl = new FormControl('', [Validators.required]);
  cidadeFormControl = new FormControl('', [Validators.required]);
  estadoFormControl = new FormControl('', [Validators.required]);
  numeroFormControl = new FormControl('', [Validators.required]);
  complementoFormControl = new FormControl('', []);
  cepFormControl = new FormControl('', [
    Validators.required,
    CustomValidators.cep()
  ]);
  signupService = inject(SignupService);

    setEditing() {
      const client = this.sessionService.meClient();
      this.nameControl.setValue(client?.name || '');
      this.emailControl.setValue(client?.email || '');
      this.phoneControl.setValue(client?.phone || '');
      this.salaryControl.setValue(String(client?.salary ?? 0));
      this.cepFormControl.setValue(client?.zipCode ?? '')
      this.ruaFormControl.setValue(client?.street ?? '')
      this.cidadeFormControl.setValue(client?.city ?? '')
      this.estadoFormControl.setValue(client?.state ?? '')
      this.complementoFormControl.setValue(client?.complement ?? '')
      this.editing.set(true);
    }

    async handleEdit() {
      if (this.nameControl.invalid || this.emailControl.invalid || this.salaryControl.invalid || this.phoneControl.invalid) {
        this.nameControl.markAsTouched();
        this.emailControl.markAsTouched();
        this.salaryControl.markAsTouched();
        return;
      }

      const cpf = this.sessionService.meClient()?.cpf;
      if (!cpf) return;

      await this.editProfileService.editProfile(
        cpf,
        this.nameControl.value!,
        this.emailControl.value!,
        this.phoneControl.value!,
        this.ruaFormControl.value!,
        this.cidadeFormControl.value!,
        this.estadoFormControl.value!,
        this.cepFormControl.value!,
        Number(this.salaryControl.value)
      );

      this.editing.set(false);
    }
}
