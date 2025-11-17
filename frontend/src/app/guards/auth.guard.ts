import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../session/session.service';

export const authGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  const user = sessionService.user();

  if (!user) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
