import {CommonModule, CurrencyPipe, DatePipe, JsonPipe} from '@angular/common';
import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
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
import {MatIcon, MatIconModule} from '@angular/material/icon';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';

interface Operation {
  dateTime: string;
  operation: string;
  origin: string;
  destination: string;
  value: number;
}

type Period = {
  date: Date;
  total: number;
  operations: Operation[];
}

@Component({
  selector: 'app-account-statement',
  providers: [provideNativeDateAdapter()],
  imports: [MatFormFieldModule, MatDatepickerModule, FormsModule, MatTableModule, MatIconModule, MatButton, MatFormField, MatLabel, MatInput, ReactiveFormsModule, JsonPipe, MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderRow, MatHeaderRowDef, MatIcon, MatRow, MatRowDef, MatTable, DatePipe, CurrencyPipe, CommonModule],
  templateUrl: './account-statement.component.html',
  styleUrl: './account-statement.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountStatementComponent {
  tables: Period[] = [{date: new Date(), total: 800, operations: [
    {dateTime: '2024-01-01 10:00', operation: 'Deposito', origin: 'Externa', destination: '1233321 (Minha Conta)', value: 1000},
    {dateTime: '2024-01-01 12:00', operation: 'Transferência', origin: '1233321 (Minha Conta)', destination: '333211212', value: -200},
  ]}, {date: new Date(new Date().setDate(new Date().getDate() - 1)), total: 1000, operations: [
    {dateTime: '2023-12-31 09:00', operation: 'Saque', origin: '1233321 (Minha Conta)', destination: 'Externa', value: -100},
    {dateTime: '2023-12-31 15:00', operation: 'Deposito', origin: 'Externa', destination: '1233321 (Minha Conta)', value: 500},
    ]}];
  displayedColumns: string[] = ['DATE_TIME', 'OPERATION', 'ORIGIN', 'DESTINATION', 'VALUE', 'SUM'];
  readonly range = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  });

  getRowClass(row: Operation): string {
  if (row.operation === 'Saque' || (row.operation === 'Transferência' && row.value < 0)) {
    return 'red-row';
  }
  if (row.operation === 'Deposito' || (row.operation === 'Transferência' && row.value > 0)) {
    return 'blue-row';
  }
  return '';
}
}
