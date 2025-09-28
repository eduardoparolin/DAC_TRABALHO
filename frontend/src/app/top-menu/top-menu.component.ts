import {Component, inject} from '@angular/core';
import {PersonIdentificationComponent} from '../person-identification/person-identification.component';
import {MatMenuModule} from '@angular/material/menu';
import {SessionService} from '../session/session.service';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {CurrencyPipe} from '@angular/common';

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
  logout() {
    this.sessionService.logout();
  }

  openEditClientDialog() {

  }
}
