import {Component, inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatProgressBar} from '@angular/material/progress-bar';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {SignupService} from './signup.service';

@Component({
  selector: 'app-signup',
  imports: [
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatInput,
    MatError,
    MatButton,
    MatProgressBar
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  service = inject(SignupService);
  emailFormControl = new FormControl('', [Validators.required, Validators.email]);
  passwordFormControl = new FormControl('', [Validators.required]);

  signup() {

  }
}
