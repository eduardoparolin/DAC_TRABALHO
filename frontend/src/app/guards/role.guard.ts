import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../session/session.service';
import { UserType } from '../session/user.model';

export const adminGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  const user = sessionService.user();

  if (!user || !user.isAdmin()) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};

export const managerGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  const user = sessionService.user();

  if (!user || !user.isManager()) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};

export const clientGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  const user = sessionService.user();

  if (!user || !user.isClient()) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};

export const adminOrManagerGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  const user = sessionService.user();

  if (!user || (!user.isAdmin() && !user.isManager())) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
