import {Component, inject} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {SessionService} from '../session/session.service';
import {UserTypePipe} from '../user-type.pipe';

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
  sessionService = inject(SessionService);

}
