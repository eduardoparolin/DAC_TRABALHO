import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {AccountsService} from './accounts.service';
import {ClientApprovalService} from '../client-approval/client-approval.service';

@Component({
  selector: 'app-accounts',
    imports: [
        MatButton,
        MatCell,
        MatCellDef,
        MatColumnDef,
        MatHeaderCell,
        MatHeaderRow,
        MatHeaderRowDef,
        MatRow,
        MatRowDef,
        MatTable
    ],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss'
})
export class AccountsComponent {
  service = inject(ClientApprovalService)
}
//
// import {Component, inject} from '@angular/core';
// import {MatButton} from "@angular/material/button";
// import {
//   MatCell,
//   MatCellDef,
//   MatColumnDef,
//   MatHeaderCell,
//   MatHeaderRow,
//   MatHeaderRowDef,
//   MatRow, MatRowDef, MatTable
// } from "@angular/material/table";
// import {AccountsService} from './accounts.service';
// import {ClientApprovalService} from '../client-approval/client-approval.service';
//
// @Component({
//   selector: 'app-accounts',
//   imports: [
//     MatButton,
//     MatCell,
//     MatCellDef,
//     MatColumnDef,
//     MatHeaderCell,
//     MatHeaderRow,
//     MatHeaderRowDef,
//     MatRow,
//     MatRowDef,
//     MatTable
//   ],
//   templateUrl: './accounts.component.html',
//   styleUrl: './accounts.component.scss'
// })
// export class AccountsComponent {
//   service = inject(ClientApprovalService)
// }
//
//
// import {Component, inject} from '@angular/core';
// import {MatButton} from "@angular/material/button";
// import {
//   MatCell,
//   MatCellDef,
//   MatColumnDef,
//   MatHeaderCell,
//   MatHeaderRow,
//   MatHeaderRowDef,
//   MatRow, MatRowDef, MatTable
// } from "@angular/material/table";
// import {AccountsService} from './accounts.service';
// import {ClientApprovalService} from '../client-approval/client-approval.service';
//
// @Component({
//   selector: 'app-accounts',
//   imports: [
//     MatButton,
//     MatCell,
//     MatCellDef,
//     MatColumnDef,
//     MatHeaderCell,
//     MatHeaderRow,
//     MatHeaderRowDef,
//     MatRow,
//     MatRowDef,
//     MatTable
//   ],
//   templateUrl: './accounts.component.html',
//   styleUrl: './accounts.component.scss'
// })
// export class AccountsComponent {
//   service = inject(ClientApprovalService)
// }
//


//REMOVER TUDO ACIMA, ESTE COMPONENTE PRECISA DE REVISAO
