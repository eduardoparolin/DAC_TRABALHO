import {CommonModule, CurrencyPipe, DatePipe} from '@angular/common';
import {ChangeDetectionStrategy, Component, OnInit, inject, signal, effect} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
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
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {AccountStatementService, Transaction} from './account-statement.service';

interface DailyStatement {
  date: Date;
  dateString: string;
  transactions: Transaction[];
  dailyChange: number;
  endBalance: number;
  isExpanded: boolean;
}

@Component({
  selector: 'app-account-statement',
  imports: [
    MatFormFieldModule,
    MatTableModule,
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
    CurrencyPipe,
    CommonModule,
    MatProgressBar,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    MatIconModule,
    ReactiveFormsModule,
    DatePipe
  ],
  providers: [DatePipe],
  templateUrl: './account-statement.component.html',
  styleUrl: './account-statement.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountStatementComponent implements OnInit {
  service = inject(AccountStatementService);
  datePipe = inject(DatePipe);

  displayedColumns: string[] = ['date', 'dailyChange', 'endBalance', 'expand'];
  expandedElement: DailyStatement | null = null;

  dailyStatements = signal<DailyStatement[]>([]);

  dateRangeForm = new FormGroup({
    startDate: new FormControl<Date>(this.getFirstDayOfMonth()),
    endDate: new FormControl<Date>(this.getLastDayOfMonth())
  });

  constructor() {
    // Update daily statements when raw statement data changes
    effect(() => {
      const statement = this.service.statement();
      if (statement) {
        this.processDailyStatements();
      }
    });
  }

  ngOnInit(): void {
    this.refresh();

    // Listen to date range changes
    this.dateRangeForm.valueChanges.subscribe(() => {
      this.processDailyStatements();
    });
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

  toggleRow(element: DailyStatement): void {
    element.isExpanded = !element.isExpanded;
    this.expandedElement = element.isExpanded ? element : null;
  }

  getFirstDayOfMonth(): Date {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth(), 1);
  }

  getLastDayOfMonth(): Date {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth() + 1, 0);
  }

  processDailyStatements(): void {
    const statement = this.service.statement();
    if (!statement) {
      this.dailyStatements.set([]);
      return;
    }

    const startDate = this.dateRangeForm.value.startDate;
    const endDate = this.dateRangeForm.value.endDate;

    if (!startDate || !endDate) {
      this.dailyStatements.set([]);
      return;
    }

    // Filter transactions by date range
    const filteredTransactions = statement.movimentacoes.filter(t => {
      const transDate = new Date(t.data);
      return transDate >= startDate && transDate <= endDate;
    });

    // Group transactions by day
    const transactionsByDay = new Map<string, Transaction[]>();
    filteredTransactions.forEach(transaction => {
      const date = new Date(transaction.data);
      const dateKey = this.datePipe.transform(date, 'yyyy-MM-dd') || '';

      if (!transactionsByDay.has(dateKey)) {
        transactionsByDay.set(dateKey, []);
      }
      transactionsByDay.get(dateKey)!.push(transaction);
    });

    // Generate daily statements for ALL days in range
    const dailyStatements: DailyStatement[] = [];
    let currentBalance = statement.saldo;

    // Sort all transactions by date (newest first) to calculate backwards
    const sortedTransactions = [...filteredTransactions].sort((a, b) =>
      new Date(b.data).getTime() - new Date(a.data).getTime()
    );

    // Calculate the starting balance for the range (work backwards from current balance)
    sortedTransactions.forEach(transaction => {
      const isDebit = transaction.tipo === 'SAQUE' ||
                     (transaction.tipo === 'TRANSFERENCIA' && transaction.origem === statement.conta);
      const isCredit = transaction.tipo === 'DEPOSITO' ||
                      (transaction.tipo === 'TRANSFERENCIA' && transaction.destino === statement.conta);

      if (isDebit) {
        currentBalance += transaction.valor;
      } else if (isCredit) {
        currentBalance -= transaction.valor;
      }
    });

    // Now go forward through the date range
    let runningBalance = currentBalance;
    const current = new Date(startDate);

    while (current <= endDate) {
      const dateKey = this.datePipe.transform(current, 'yyyy-MM-dd') || '';
      const dateString = this.datePipe.transform(current, 'dd/MM/yyyy') || '';
      const dayTransactions = transactionsByDay.get(dateKey) || [];

      // Sort day transactions by time
      dayTransactions.sort((a, b) => new Date(a.data).getTime() - new Date(b.data).getTime());

      // Calculate daily change
      let dailyChange = 0;
      dayTransactions.forEach(transaction => {
        const isDebit = transaction.tipo === 'SAQUE' ||
                       (transaction.tipo === 'TRANSFERENCIA' && transaction.origem === statement.conta);
        const isCredit = transaction.tipo === 'DEPOSITO' ||
                        (transaction.tipo === 'TRANSFERENCIA' && transaction.destino === statement.conta);

        if (isDebit) {
          dailyChange -= transaction.valor;
          runningBalance -= transaction.valor;
        } else if (isCredit) {
          dailyChange += transaction.valor;
          runningBalance += transaction.valor;
        }
      });

      dailyStatements.push({
        date: new Date(current),
        dateString: dateString,
        transactions: dayTransactions,
        dailyChange: dailyChange,
        endBalance: runningBalance,
        isExpanded: false
      });

      current.setDate(current.getDate() + 1);
    }

    this.dailyStatements.set(dailyStatements);
  }

  getTransactionRowClass(transaction: Transaction): string {
    const statement = this.service.statement();
    if (!statement) {
      return '';
    }

    const isDebit = transaction.tipo === 'SAQUE' ||
                   (transaction.tipo === 'TRANSFERENCIA' && transaction.origem === statement.conta);
    if (isDebit) {
      return 'red-text';
    }

    const isCredit = transaction.tipo === 'DEPOSITO' ||
                    (transaction.tipo === 'TRANSFERENCIA' && transaction.destino === statement.conta);
    if (isCredit) {
      return 'blue-text';
    }

    return '';
  }
}
