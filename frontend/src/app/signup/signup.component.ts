import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
  MatError,
  MatFormField,
  MatInput,
  MatLabel,
  MatSuffix,
} from '@angular/material/input';
import { MatProgressBar } from '@angular/material/progress-bar';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { SignupService } from './signup.service';
import { NgxMaskDirective } from 'ngx-mask';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { CustomValidators } from '../utils/validators';
import { ErrorHandlerService } from '../utils/error-handler.service';

@Component({
  selector: 'app-signup',
  imports: [
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatInput,
    MatError,
    MatButton,
    MatProgressBar,
    NgxMaskDirective,
    MatSuffix,
    MatProgressSpinner,
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
})
export class SignupComponent implements OnInit, OnDestroy {
  service = inject(SignupService);
  private router = inject(Router);
  private errorHandler = inject(ErrorHandlerService);
  private destroy$ = new Subject<void>();

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  cpfFormControl = new FormControl('', [
    Validators.required,
    CustomValidators.cpf()
  ]);
  phoneFormControl = new FormControl('', [
    Validators.required,
    CustomValidators.phone()
  ]);
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
  nameFormControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3)
  ]);

  ngOnInit(): void {
    this.cepFormControl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.cepFormControl.invalid) return;
        this.service.fetchAddress(this.cepFormControl.value!).then((address) => {
          if (address) {
            this.ruaFormControl.setValue(address.street);
            this.bairroFormControl.setValue(address.neighborhood);
            this.cidadeFormControl.setValue(address.city);
            this.estadoFormControl.setValue(address.state);
          }
        });
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  async signup() {
    const formControls = [
      this.emailFormControl,
      this.cpfFormControl,
      this.phoneFormControl,
      this.ruaFormControl,
      this.bairroFormControl,
      this.cidadeFormControl,
      this.estadoFormControl,
      this.numeroFormControl,
      this.cepFormControl,
      this.nameFormControl,
    ];

    formControls.forEach((control) => control.markAsTouched());

    const isFormValid = formControls.every((control) => control.valid);

    if (!isFormValid) {
      this.errorHandler.handleError('Por favor, preencha todos os campos corretamente.');
      return;
    }

    try {
      const signupData = {
        nome: this.nameFormControl.value!,
        email: this.emailFormControl.value!,
        cpf: this.cpfFormControl.value!.replace(/\D/g, ''),
        telefone: this.phoneFormControl.value!.replace(/\D/g, ''),
        endereco: {
          cep: this.cepFormControl.value!.replace(/\D/g, ''),
          rua: this.ruaFormControl.value!,
          bairro: this.bairroFormControl.value!,
          cidade: this.cidadeFormControl.value!,
          estado: this.estadoFormControl.value!,
          numero: this.numeroFormControl.value!,
          complemento: this.complementoFormControl.value || ''
        }
      };

      await this.service.signup(signupData);
      this.errorHandler.handleSuccess('Cadastro realizado com sucesso!');
      this.router.navigate(['/login']);
    } catch (error) {
      this.errorHandler.handleError(error as Error);
    }
  }
}
