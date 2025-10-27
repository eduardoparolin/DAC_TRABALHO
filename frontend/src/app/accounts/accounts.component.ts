import { Component, inject, OnInit } from '@angular/core';
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
import { AccountsService } from './accounts.service';

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
export class AccountsComponent implements OnInit {
  service = inject(AccountsService);

  get displayedColumns() {
    return this.service.displayedColumns;
  }

  ngOnInit() {
    this.service.ngOnInit();
  }

  approveClient(element: any) {
    this.service.approveClient(element.id);
  }

  rejectClient(element: any) {
    this.service.rejectClient(element.id);
  }
}
