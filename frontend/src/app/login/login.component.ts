import {Component, inject, OnInit} from '@angular/core';
import {LoginService} from './login.service';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  loginService = inject(LoginService);
  constructor() { }

  ngOnInit(): void {
    this.loginService.login('a', 'n').then((res) => {
      console.log(res);
    })
  }

}
