import {Component, inject} from '@angular/core';
import {PersonIdentificationComponent} from '../person-identification/person-identification.component';
import {MatMenuModule} from '@angular/material/menu';
import {SessionService} from '../session/session.service';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {CurrencyPipe} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {EditProfileComponent} from '../edit-profile/edit-profile.component';
import {ClientAccountService} from '../utils/client-account.service';
import {ClientsService} from '../clients/clients.service';

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
  clientAccountService = inject(ClientAccountService)
  clientService = inject(ClientsService)
  dialog = inject(MatDialog);
  logout() {
    this.clientAccountService.account.set(null);
    this.clientService.currentClient.set(null);
    this.clientService.clients.set([]);
    this.clientService.filteredClients.set([]);
    this.sessionService.logout();
  }

  openEditClientDialog() {
    this.dialog.open(EditProfileComponent);
  }
}
