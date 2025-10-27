import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import { provideEnvironmentNgxMask } from 'ngx-mask';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptor} from '../../interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideEnvironmentNgxMask(),
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
