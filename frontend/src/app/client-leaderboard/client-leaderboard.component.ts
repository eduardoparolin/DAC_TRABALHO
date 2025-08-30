import {Component, inject, OnInit} from '@angular/core';
import {CardComponent} from './card/card.component';
import {ClientLeaderboardService} from './client-leaderboard.service';

@Component({
  selector: 'app-client-leaderboard',
  imports: [
    CardComponent
  ],
  templateUrl: './client-leaderboard.component.html',
  styleUrl: './client-leaderboard.component.scss'
})
export class ClientLeaderboardComponent implements OnInit {
  service = inject(ClientLeaderboardService);
    ngOnInit(): void {
        this.service.getAllClients();
    }


}
