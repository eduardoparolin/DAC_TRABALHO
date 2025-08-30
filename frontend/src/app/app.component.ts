import {Component, inject} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {MatButton} from '@angular/material/button';
import {TopMenuComponent} from './top-menu/top-menu.component';
import {SessionService} from './session/session.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatButton, RouterLink, TopMenuComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  sessionService = inject(SessionService);
  title = 'frontend';
}
