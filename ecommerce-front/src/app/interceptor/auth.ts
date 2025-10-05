import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {catchError, throwError} from 'rxjs';
import {Router} from '@angular/router';
import {AuthService} from '../service/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.obtenirToken();

  // Clone la requête et ajoute le token si disponible
  if (token && !req.url.includes('/account') && !req.url.includes('/token')) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401) {
        authService.seDeconnecter();
        router.navigate(['/connexion']);
      }
      return throwError(() => error);
    })
  );
};
