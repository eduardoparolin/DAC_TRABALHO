import {Component, inject} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {WithdrawService} from './withdraw.service';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInputModule, MatLabel} from '@angular/material/input';
import {NgxCurrencyDirective} from 'ngx-currency';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-withdraw',
  imports: [NgxCurrencyDirective, MatFormField, MatInputModule, ReactiveFormsModule, MatLabel, MatButton, CurrencyPipe],
  templateUrl: './withdraw.component.html',
  styleUrl: './withdraw.component.scss'
})
export class WithdrawComponent {
  service = inject(WithdrawService);
  valueFormControl = new FormControl<null | number>(0, Validators.required);

  async withdraw(){
    if (this.valueFormControl.valid && this.valueFormControl.value){
      const result = await this.service.withdraw(this.valueFormControl.value);
      if (result) {
        this.valueFormControl.reset();
      }
    }
  }
}
