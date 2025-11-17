import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  static cpf(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const cpf = control.value?.replace(/\D/g, '');

      if (!cpf || cpf.length !== 11) {
        return { cpfInvalid: true };
      }

      if (/^(\d)\1+$/.test(cpf)) {
        return { cpfInvalid: true };
      }

      let sum = 0;
      let remainder;

      for (let i = 1; i <= 9; i++) {
        sum += parseInt(cpf.substring(i - 1, i)) * (11 - i);
      }

      remainder = (sum * 10) % 11;
      if (remainder === 10 || remainder === 11) {
        remainder = 0;
      }

      if (remainder !== parseInt(cpf.substring(9, 10))) {
        return { cpfInvalid: true };
      }

      sum = 0;
      for (let i = 1; i <= 10; i++) {
        sum += parseInt(cpf.substring(i - 1, i)) * (12 - i);
      }

      remainder = (sum * 10) % 11;
      if (remainder === 10 || remainder === 11) {
        remainder = 0;
      }

      if (remainder !== parseInt(cpf.substring(10, 11))) {
        return { cpfInvalid: true };
      }

      return null;
    };
  }

  static phone(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const phone = control.value?.replace(/\D/g, '');

      if (!phone) {
        return null;
      }

      if (phone.length !== 10 && phone.length !== 11) {
        return { phoneInvalid: true };
      }

      return null;
    };
  }

  static cep(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const cep = control.value?.replace(/\D/g, '');

      if (!cep) {
        return null;
      }

      if (cep.length !== 8) {
        return { cepInvalid: true };
      }

      return null;
    };
  }

  static minValue(min: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = parseFloat(control.value);

      if (isNaN(value)) {
        return null;
      }

      if (value < min) {
        return { minValue: { min, actual: value } };
      }

      return null;
    };
  }

  static maxValue(max: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = parseFloat(control.value);

      if (isNaN(value)) {
        return null;
      }

      if (value > max) {
        return { maxValue: { max, actual: value } };
      }

      return null;
    };
  }

  static accountNumber(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const accountNumber = control.value?.replace(/\D/g, '');

      if (!accountNumber) {
        return null;
      }

      if (accountNumber.length < 4 || accountNumber.length > 12) {
        return { accountNumberInvalid: true };
      }

      return null;
    };
  }
}
