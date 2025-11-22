import {Component, inject, signal, OnInit} from '@angular/core';
import {SessionService} from '../session/session.service';
import {MatButton} from '@angular/material/button';
import {CurrencyPipe} from '@angular/common';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import { EditProfileService, ClientProfile } from './edit-profile.service';
import {NgxMaskDirective, NgxMaskPipe} from 'ngx-mask';
import {NgxCurrencyDirective} from 'ngx-currency';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {CustomValidators} from '../utils/validators';
import {SignupService} from '../signup/signup.service';
import { CpfPipe } from '../utils/cpf.pipe';

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
    NgxCurrencyDirective,
    MatError,
    MatProgressSpinner,
    MatSuffix,
    CpfPipe,
    NgxMaskDirective,
    NgxMaskPipe,
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.scss'
})
export class EditProfileComponent implements OnInit {
  sessionService = inject(SessionService);
  editProfileService = inject(EditProfileService);
  editing = signal<boolean>(false);
  profileData = signal<ClientProfile | null>(null);

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

  async ngOnInit() {
    await this.loadProfile();
  }

  async loadProfile() {
    const profile = await this.editProfileService.getClientProfile();
    if (profile) {
      this.profileData.set(profile);
      this.populateForm(profile);
    }
  }

  private populateForm(profile: ClientProfile) {
    this.nameControl.setValue(profile.cliente.nome);
    this.emailControl.setValue(profile.cliente.email);
    this.phoneControl.setValue(profile.cliente.telefone);
    this.salaryControl.setValue(String(profile.cliente.salario));
    this.cepFormControl.setValue(profile.cliente.endereco.CEP);
    this.ruaFormControl.setValue(profile.cliente.endereco.rua);
    this.numeroFormControl.setValue(profile.cliente.endereco.numero);
    this.complementoFormControl.setValue(profile.cliente.endereco.complemento);
    this.cidadeFormControl.setValue(profile.cliente.endereco.cidade);
    this.estadoFormControl.setValue(profile.cliente.endereco.estado);
  }

  setEditing() {
    this.editing.set(true);
  }

  cancelEditing() {
    const profile = this.profileData();
    if (profile) {
      this.populateForm(profile);
    }
    this.editing.set(false);
  }

  async handleEdit() {
    if (this.nameControl.invalid || this.emailControl.invalid || this.salaryControl.invalid || this.phoneControl.invalid) {
      this.nameControl.markAsTouched();
      this.emailControl.markAsTouched();
      this.salaryControl.markAsTouched();
      return;
    }

    const profile = this.profileData();
    if (!profile) return;

    // currencyMask retorna o valor como número ou string
    const salaryValue = typeof this.salaryControl.value === 'number'
      ? this.salaryControl.value
      : Number(this.salaryControl.value);

    await this.editProfileService.editProfile(
      profile.cliente.cpf,
      this.nameControl.value!,
      this.emailControl.value!,
      this.phoneControl.value!,
      this.ruaFormControl.value!,
      this.cidadeFormControl.value!,
      this.estadoFormControl.value!,
      this.cepFormControl.value!,
      salaryValue
    );

    // Recarrega os dados do perfil após a edição
    await this.loadProfile();
    this.editing.set(false);
  }
}
