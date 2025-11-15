import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { ErrorHandlerService } from '../utils/error-handler.service';
import { SessionService } from '../session/session.service';
import { idText } from 'typescript';

@Injectable({
  providedIn: 'root',
})
export class EditProfileService {
  http = inject(HttpClient);
  session = inject(SessionService);
  errorHandler = inject(ErrorHandlerService);
  loading = signal(false);

  async editProfile(cpf: string, name: string, email: string, salary: number): Promise<void> {
    this.loading.set(true);
    const user = this.session.getUser();
    try {
      const response = await lastValueFrom(
        this.http.put(`${environment.baseUrl}/clientes`, {
          cpf,
          name,
          email,
          salary,
          clientId: user?.id,
        })
      );
      this.loading.set(false);
      this.errorHandler.handleSuccess('Perfil atualizado com sucesso!');
    } catch (error) {
      this.loading.set(false);
      this.errorHandler.handleError(error, { showError: true });
    }
  }
}
