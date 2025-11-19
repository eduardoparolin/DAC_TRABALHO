import {Component, inject} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInputModule, MatLabel} from '@angular/material/input';
import {NgxCurrencyDirective} from 'ngx-currency';
import {TransferService} from './transfer.service';
import {CustomValidators} from '../utils/validators';
import {ErrorHandlerService} from '../utils/error-handler.service';
import {CurrencyPipe} from '@angular/common';
import {ClientAccountService} from '../utils/client-account.service';

@Component({
  selector: 'app-transfer',
  imports: [NgxCurrencyDirective, MatFormField, MatInputModule, ReactiveFormsModule, MatLabel, MatButton, CurrencyPipe],
  templateUrl: './transfer.component.html',
  styleUrl: './transfer.component.scss'
})
export class TransferComponent {
  service = inject(TransferService);
  accountService = inject(ClientAccountService);
  errorHandler = inject(ErrorHandlerService);
  valueFormControl = new FormControl<null | number>(0, [
    Validators.required,
    CustomValidators.minValue(0.01)
  ]);
  accountNumberFormControl = new FormControl<null | string>(null, [
    Validators.required,
    CustomValidators.accountNumber()
  ]);

  get balance(): number {
    return this.accountService.account()?.saldo ?? 0;
  }

  async transfer() {
    this.valueFormControl.markAsTouched();
    this.accountNumberFormControl.markAsTouched();

    if (!this.valueFormControl.valid || !this.accountNumberFormControl.valid) {
      this.errorHandler.handleError('Por favor, preencha todos os campos corretamente.');
      return;
    }

    if (this.valueFormControl.value && this.accountNumberFormControl.value) {
      const result = await this.service.transfer(
        this.valueFormControl.value,
        this.accountNumberFormControl.value
      );
      if (result) {
        this.valueFormControl.reset();
        this.accountNumberFormControl.reset();
      }
    }
  }
}
