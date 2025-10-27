import {Component, inject, signal} from '@angular/core';
import {SessionService} from '../session/session.service';
import {MatButton} from '@angular/material/button';
import {CurrencyPipe} from '@angular/common';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';

@Component({
  selector: 'app-edit-profile',
  imports: [
    MatButton,
    CurrencyPipe,
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatFormField,
    ReactiveFormsModule
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.scss'
})
export class EditProfileComponent {
  sessionService = inject(SessionService);
  editing = signal<boolean>(false);

  nameControl = new FormControl('', [Validators.required]);
  emailControl = new FormControl('', [Validators.required, Validators.email]);
  wageControl = new FormControl('', [Validators.required]);

  setEditing () {
    this.nameControl.setValue(this.sessionService.meClient()?.name || '');
    this.emailControl.setValue(this.sessionService.meClient()?.email || '');
    this.wageControl.setValue('0');
    this.editing.set(true);
  }

  protected readonly name = name;
}
