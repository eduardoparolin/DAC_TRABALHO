import {Component, inject} from '@angular/core';
import {PersonIdentificationComponent} from '../person-identification/person-identification.component';
import {MatMenuModule} from '@angular/material/menu';
import {SessionService} from '../session/session.service';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {CurrencyPipe} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {EditProfileComponent} from '../edit-profile/edit-profile.component';

@Component({
  selector: 'app-top-menu',
  imports: [
    PersonIdentificationComponent,
    MatMenuModule,
    RouterLink,
    RouterLinkActive,
    CurrencyPipe
  ],
  templateUrl: './top-menu.component.html',
  styleUrl: './top-menu.component.scss'
})
export class TopMenuComponent {
  sessionService = inject(SessionService);
  dialog = inject(MatDialog);
  logout() {
    this.sessionService.logout();
  }

  openEditClientDialog() {
    this.dialog.open(EditProfileComponent);
  }
}
