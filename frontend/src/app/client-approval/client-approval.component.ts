import {Component, inject, OnInit} from '@angular/core';
import {
  MatTableModule
} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {ReactiveFormsModule} from '@angular/forms';
import {ClientApprovalService, PendingClient} from './client-approval.service';
import {MatButton} from '@angular/material/button';
import {MatProgressBar} from '@angular/material/progress-bar';

@Component({
  selector: 'app-client-approval',
  imports: [MatTableModule, MatIconModule, ReactiveFormsModule, MatButton, MatProgressBar],
  templateUrl: './client-approval.component.html',
  styleUrl: './client-approval.component.scss'
})
export class ClientApprovalComponent implements OnInit {
    service = inject(ClientApprovalService)

    displayedColumns: string[] = ['CPF', 'NAME', 'EMAIL', 'WAGE', 'ACTIONS'];

    ngOnInit() {
      this.service.getAllClients();
    }

    approveClient(client: PendingClient) {
      this.service.approveClient(client);
    }

    rejectClient(client: PendingClient) {
      this.service.rejectClient(client);
    }
}
