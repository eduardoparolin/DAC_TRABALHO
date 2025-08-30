import { Pipe, PipeTransform } from '@angular/core';
import {UserType} from './session/user.model';

@Pipe({
  name: 'userType'
})
export class UserTypePipe implements PipeTransform {

  transform(value: UserType | undefined, ...args: unknown[]): string {
    if (!value) {
      return '';
    }
    switch (value) {
      case UserType.ADMIN:
        return 'Administrador';
      case UserType.CLIENT:
        return 'Cliente';
      case UserType.MANAGER:
        return 'Gerente';
    }
  }

}
