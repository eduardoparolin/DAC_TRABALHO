import {Component, inject} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {SessionService} from '../session/session.service';
import {UserTypePipe} from '../user-type.pipe';
import {ClientsService} from '../clients/clients.service';

@Component({
  selector: 'app-person-identification',
  imports: [
    MatIcon,
    UserTypePipe
  ],
  templateUrl: './person-identification.component.html',
  styleUrl: './person-identification.component.scss'
})
export class PersonIdentificationComponent {
  clientService = inject(ClientsService);
  sessionService = inject(SessionService);
  myName() {
    if (this.sessionService.user()?.isClient()) {
      return this.clientService.currentClient()?.nome ?? this.sessionService.user()?.name;
    }
    return this.sessionService.user()?.name;
  }
}
