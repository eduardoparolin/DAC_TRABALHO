import {Component, inject} from '@angular/core';
import {PersonIdentificationComponent} from '../person-identification/person-identification.component';
import {MatMenuModule} from '@angular/material/menu';
import {SessionService} from '../session/session.service';
import {MatButton} from '@angular/material/button';
import {RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-top-menu',
  imports: [
    PersonIdentificationComponent,
    MatMenuModule,
    MatButton,
    RouterLink,
    RouterLinkActive
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
