import {Component, input} from '@angular/core';
import {CurrencyPipe, NgClass} from "@angular/common";
import {MatIcon} from '@angular/material/icon';
import {Client} from '../../clients/client.model';

@Component({
  selector: 'app-card',
  imports: [
    CurrencyPipe,
    MatIcon,
    NgClass,
  ],
  templateUrl: './card.component.html',
  styleUrl: './card.component.scss'
})
export class CardComponent {
  place = input.required<0 | 1 | 2>();
  client = input.required<Client>();
}
