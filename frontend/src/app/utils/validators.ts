import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  static minValue(min: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (value === null || value === undefined || value === '') {
        return null;
      }
      return value < min ? { minValue: { min, actual: value } } : null;
    };
  }

  static accountNumber(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }
      const accountNumberRegex = /^\d{4,20}$/;
      return accountNumberRegex.test(value) ? null : { accountNumber: true };
    };
  }

  static cpf(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }
      const cpf = value.replace(/\D/g, '');
      if (cpf.length !== 11) {
        return { cpf: true };
      }
      if (/^(\d)\1{10}$/.test(cpf)) {
        return { cpf: true };
      }
      return null;
    };
  }

  static phone(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }
      const phone = value.replace(/\D/g, '');
      return phone.length >= 10 && phone.length <= 11 ? null : { phone: true };
    };
  }

  static cep(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }
      const cep = value.replace(/\D/g, '');
      return cep.length === 8 ? null : { cep: true };
    };
  }
}
