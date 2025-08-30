import {Component, inject} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgxCurrencyDirective} from 'ngx-currency';
import {MatFormField, MatInputModule, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {DepositService} from './deposit.service';

@Component({
  selector: 'app-deposit',
  imports: [NgxCurrencyDirective, MatFormField, MatInputModule, ReactiveFormsModule, MatLabel, MatButton],
  templateUrl: './deposit.component.html',
  styleUrl: './deposit.component.scss'
})
export class DepositComponent {
  service = inject(DepositService);
  valueFormControl = new FormControl<null | number>(0, Validators.required);

  deposit(){
    if (this.valueFormControl.valid && this.valueFormControl.value){
      this.service.deposit(this.valueFormControl.value);
    }
  }
}
