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
  cidadeFormControl = new FormControl('', [Validators.required]);
  estadoFormControl = new FormControl('', [Validators.required]);
  complementoFormControl = new FormControl('', []);
  cepFormControl = new FormControl('', [
    Validators.required,
    CustomValidators.cep()
  ]);
  nameFormControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3)
  ]);
  salaryFormControl = new FormControl(0, [
    Validators.required,
    Validators.min(0)
  ]);

  ngOnInit(): void {
    this.cepFormControl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.cepFormControl.invalid) return;
        this.service.fetchAddress(this.cepFormControl.value!).then((address) => {
          if (address) {
            this.ruaFormControl.setValue(address.street);
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
      this.cidadeFormControl,
      this.estadoFormControl,
      this.cepFormControl,
      this.nameFormControl,
      this.salaryFormControl,
    ];

    formControls.forEach((control) => control.markAsTouched());

    const isFormValid = formControls.every((control) => control.valid);

    if (!isFormValid) {
      this.errorHandler.handleError('Por favor, preencha todos os campos corretamente.');
      return;
    }

    try {
      const cpf = this.cpfFormControl.value!.replace(/\D/g, '');
      const telefone = this.phoneFormControl.value!.replace(/\D/g, '');
      const CEP = this.cepFormControl.value!.replace(/\D/g, '');
      const nome = this.nameFormControl.value!.trim();
      const salario = Number(this.salaryFormControl.value!);
      const cidade = this.cidadeFormControl.value!.trim();
      const estado = this.estadoFormControl.value!.trim().toUpperCase();
      const rua = this.ruaFormControl.value!.trim();
      const complemento = this.complementoFormControl.value?.trim();

      const signupData = {
        cpf,
        nome,
        email: this.emailFormControl.value!.trim(),
        telefone,
        salario,
        endereco: rua,
        complemento: complemento || undefined,
        CEP,
        cidade,
        estado,
      };

      await this.service.signup(signupData);
      this.errorHandler.handleSuccess('Cadastro iniciado com sucesso! Você receberá um email com suas credenciais.');
      this.router.navigate(['/login']);
    } catch (error) {
      this.errorHandler.handleError(error as Error);
    }
  }
}
