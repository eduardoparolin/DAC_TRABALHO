import {Component, inject} from '@angular/core';
import {PersonIdentificationComponent} from '../person-identification/person-identification.component';
import {MatMenuModule} from '@angular/material/menu';
import {SessionService} from '../session/session.service';

@Component({
  selector: 'app-top-menu',
  imports: [
    PersonIdentificationComponent,
    MatMenuModule
  ],
  templateUrl: './top-menu.component.html',
  styleUrl: './top-menu.component.scss'
})
export class TopMenuComponent {
  sessionService = inject(SessionService);
  logout() {
    this.sessionService.logout();
  }
}
