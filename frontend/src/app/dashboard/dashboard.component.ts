import {Component, inject, OnInit} from '@angular/core';
import {MatTableModule} from '@angular/material/table';
import {DashboardService} from './dashboard.service';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [MatTableModule, CurrencyPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  service = inject(DashboardService);
  displayedColumns: string[] = ['MANAGER_NAME', 'CLIENT_NUMBER', 'NET_POSITIVE', 'NET_NEGATIVE'];

  ngOnInit(): void {
    this.service.getAllDashboardManagers();
  }
}
