import { Pipe, PipeTransform } from '@angular/core';
import { UserType } from './session/user.model';

@Pipe({
  name: 'userType',
})
export class UserTypePipe implements PipeTransform {
  transform(value: UserType | undefined, ...args: unknown[]): string {
    if (!value) {
      return '';
    }
    switch (value) {
      case UserType.ADMINISTRADOR:
        return 'Administrador';
      case UserType.CLIENTE:
        return 'Cliente';
      case UserType.GERENTE:
        return 'Gerente';
    }
  }
}
