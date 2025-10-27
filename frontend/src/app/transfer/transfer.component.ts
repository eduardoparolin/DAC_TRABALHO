import {Component, inject} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInputModule, MatLabel} from '@angular/material/input';
import {NgxCurrencyDirective} from 'ngx-currency';
import {TransferService} from './transfer.service';

@Component({
  selector: 'app-transfer',
  imports: [NgxCurrencyDirective, MatFormField, MatInputModule, ReactiveFormsModule, MatLabel, MatButton],
  templateUrl: './transfer.component.html',
  styleUrl: './transfer.component.scss'
})
export class TransferComponent {
  service = inject(TransferService);
  valueFormControl = new FormControl<null | number>(0, Validators.required);
  accountNumberFormControl = new FormControl<null | string>(null, Validators.required);

  async transfer() {
    if (this.valueFormControl.valid && this.valueFormControl.value){
      const result = await this.service.transfer(this.valueFormControl.value);
      if (result) {
        this.valueFormControl.reset();
        this.accountNumberFormControl.reset();
      }
    }
  }
}
