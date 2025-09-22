import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
// import { AuthService } from './authentication/auth.service';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('login') || req.url.includes('signup')) {
    return next(req);
  }

  // const authService = inject(AuthService);
  const router = inject(Router);

  const authToken = '';
  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${authToken}` },
  });
  return next(authReq).pipe(
    catchError((error) => {
      if (error.status === 401 || error.status === 403) {
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
