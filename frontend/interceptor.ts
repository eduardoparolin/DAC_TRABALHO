import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('login') || req.url.includes('signup') || req.url.includes('viacep')) {
    return next(req);
  }

  const router = inject(Router);

  const userDataString = localStorage.getItem('session');
  let authToken = '';

  if (userDataString) {
    try {
      const userData = JSON.parse(userDataString);
      authToken =
        userData.accessToken ||
        userData.access_token ||
        userData.token ||
        '';
    } catch (error) {
      console.error('Error parsing user data from localStorage', error);
    }
  }

  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${authToken}` },
  });

  return next(authReq).pipe(
    catchError((error) => {
      if (error.status === 401 || error.status === 403) {
        localStorage.removeItem('user');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
