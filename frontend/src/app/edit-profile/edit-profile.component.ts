import {Component, inject, signal} from '@angular/core';
import {SessionService} from '../session/session.service';
import {MatButton} from '@angular/material/button';
import {CurrencyPipe} from '@angular/common';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import { EditProfileService } from './edit-profile.service';
import {NgxMaskDirective} from 'ngx-mask';

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
    NgxMaskDirective
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

    setEditing() {
      const client = this.sessionService.meClient();
      this.nameControl.setValue(client?.name || '');
      this.emailControl.setValue(client?.email || '');
      this.salaryControl.setValue(String(client?.salary ?? 0));
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
        Number(this.salaryControl.value)
      );

      this.editing.set(false);
    }
}
