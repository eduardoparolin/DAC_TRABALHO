import {inject, Injectable} from '@angular/core';
import {ClientApprovalService, PendingClient} from '../client-approval/client-approval.service';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {

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
