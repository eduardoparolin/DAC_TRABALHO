import { Component, inject, OnInit } from '@angular/core';
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
export class SignupComponent implements OnInit {
  service = inject(SignupService);
  private router = inject(Router);
  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  cpfFormControl = new FormControl('', [Validators.required]);
  phoneFormControl = new FormControl('', [Validators.required]);
  ruaFormControl = new FormControl('', [Validators.required]);
  bairroFormControl = new FormControl('', [Validators.required]);
  cidadeFormControl = new FormControl('', [Validators.required]);
  estadoFormControl = new FormControl('', [Validators.required]);
  numeroFormControl = new FormControl('', [Validators.required]);
  complementoFormControl = new FormControl('', []);
  cepFormControl = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
  ]);
  nameFormControl = new FormControl('', [Validators.required]);

  ngOnInit(): void {
    this.cepFormControl.valueChanges.subscribe(() => {
      if (this.cepFormControl.invalid) return;
      this.service.fetchAddress(this.cepFormControl.value!).then((address) => {
        console.log(address);
        if (address) {
          this.ruaFormControl.setValue(address.street);
          this.bairroFormControl.setValue(address.neighborhood);
          this.cidadeFormControl.setValue(address.city);
          this.estadoFormControl.setValue(address.state);
        }
      });
    });
  }

  signup() {
    this.router.navigate(['/login']);
  }
}
