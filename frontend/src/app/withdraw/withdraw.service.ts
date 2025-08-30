import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WithdrawService {

  constructor() { }

  withdraw(amount: number): void {
    console.log(`Withdrew amount: ${amount}`);
  }
}
