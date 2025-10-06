import {inject, Injectable} from '@angular/core';
import {ClientApprovalService} from '../client-approval/client-approval.service';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {

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
