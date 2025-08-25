import {Component, inject, OnInit} from '@angular/core';
import {LoginService} from './login.service';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {Router} from '@angular/router';
@Component({
  selector: 'app-login',
  imports: [
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatInput,
    MatError,
    MatButton
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  loginService = inject(LoginService);
  router = inject(Router);
  constructor() { }

  emailFormControl = new FormControl('', [Validators.required, Validators.email]);
  passwordFormControl = new FormControl('', [Validators.required]);

  ngOnInit(): void {}

  async login() {
    if (this.emailFormControl.valid && this.passwordFormControl.valid) {
      await this.loginService.login(this.emailFormControl.value!, this.passwordFormControl.value!);
    } else {
      this.emailFormControl.markAsTouched();
      this.passwordFormControl.markAsTouched();
    }
  }

}
