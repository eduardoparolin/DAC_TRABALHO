import {Component, inject, OnInit} from '@angular/core';
import {
  MatTableModule
} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {ReactiveFormsModule} from '@angular/forms';
import {ClientApprovalService} from './client-approval.service';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-client-approval',
  imports: [MatTableModule, MatIconModule, ReactiveFormsModule, MatButton],
  templateUrl: './client-approval.component.html',
  styleUrl: './client-approval.component.scss'
})
export class ClientApprovalComponent implements OnInit {
    service = inject(ClientApprovalService)

    displayedColumns: string[] = ['CPF', 'NAME', 'EMAIL', 'WAGE', 'ACTIONS'];

    ngOnInit() {
      this.service.getAllClients();
    }

    approveClient(clientId: string) {
      this.service.approveClient(clientId);
    }

    rejectClient(clientId: string) {
      this.service.rejectClient(clientId);
    }
}
