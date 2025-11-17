import {CommonModule, CurrencyPipe} from '@angular/common';
import {ChangeDetectionStrategy, Component, OnInit, inject} from '@angular/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable, MatTableModule
} from '@angular/material/table';
import {MatButton} from '@angular/material/button';
import {MatProgressBar} from '@angular/material/progress-bar';
import {AccountStatementService, Transaction} from './account-statement.service';

@Component({
  selector: 'app-account-statement',
  imports: [MatFormFieldModule, MatTableModule, MatButton, MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable, CurrencyPipe, CommonModule, MatProgressBar],
  templateUrl: './account-statement.component.html',
  styleUrl: './account-statement.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountStatementComponent implements OnInit {
  service = inject(AccountStatementService);
  displayedColumns: string[] = ['DATE_TIME', 'OPERATION', 'ORIGIN', 'DESTINATION', 'VALUE'];

  ngOnInit(): void {
    this.refresh();
  }

  get transactions(): Transaction[] {
    return this.service.statement()?.movimentacoes ?? [];
  }

  get balance(): number {
    return this.service.statement()?.saldo ?? 0;
  }

  refresh(): void {
    this.service.loadStatement();
  }

  getRowClass(row: Transaction): string {
    if (row.tipo === 'SAQUE' || (row.tipo === 'TRANSFERENCIA' && row.valor < 0)) {
      return 'red-row';
    }
    if (row.tipo === 'DEPOSITO' || (row.tipo === 'TRANSFERENCIA' && row.valor > 0)) {
      return 'blue-row';
    }
    return '';
  }
}
