import {Component, inject, OnInit} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {WithdrawService} from './withdraw.service';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInputModule, MatLabel} from '@angular/material/input';
import {NgxCurrencyDirective} from 'ngx-currency';
import {CurrencyPipe} from '@angular/common';
import {ClientAccountService} from '../utils/client-account.service';

@Component({
  selector: 'app-withdraw',
  imports: [NgxCurrencyDirective, MatFormField, MatInputModule, ReactiveFormsModule, MatLabel, MatButton, CurrencyPipe],
  templateUrl: './withdraw.component.html',
  styleUrl: './withdraw.component.scss'
})
export class WithdrawComponent implements OnInit {
  service = inject(WithdrawService);
  accountService = inject(ClientAccountService);
  valueFormControl = new FormControl<null | number>(0, Validators.required);

  ngOnInit(): void {
    this.accountService.loadAccount().catch(() => {});
  }

  get balance(): number {
    return this.accountService.account()?.saldo ?? 0;
  }

  async withdraw(){
    if (this.valueFormControl.valid && this.valueFormControl.value){
      const result = await this.service.withdraw(this.valueFormControl.value);
      if (result) {
        this.valueFormControl.reset();
      }
    }
  }
}
