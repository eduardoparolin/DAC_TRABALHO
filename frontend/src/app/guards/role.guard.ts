import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../session/session.service';
import { UserType } from '../session/user.model';

export const adminGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  const user = sessionService.getUser();

  if (user && user.isAdmin()) {
    return true;
  }

  router.navigate(['/']);
  return false;
};

export const managerGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  const user = sessionService.getUser();

  if (user && user.isManager()) {
    return true;
  }

  router.navigate(['/']);
  return false;
};

export const clientGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  const user = sessionService.getUser();

  if (user && user.isClient()) {
    return true;
  }

  router.navigate(['/']);
  return false;
};

export const adminOrManagerGuard: CanActivateFn = (route, state) => {
  const sessionService = inject(SessionService);
  const router = inject(Router);
  const user = sessionService.getUser();

  if (user && (user.isAdmin() || user.isManager())) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
