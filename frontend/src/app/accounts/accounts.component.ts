import { Component } from '@angular/core';
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

}
