import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';

export interface ErrorConfig {
  duration?: number;
  action?: string;
  showError?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  private snackBar = inject(MatSnackBar);

  handleError(
    error: Error | HttpErrorResponse | string,
    config: ErrorConfig = {}
  ): void {
    const {
      duration = 5000,
      action = 'Fechar',
      showError = true
    } = config;

    let message = '';

    if (error instanceof HttpErrorResponse) {
      message = this.getHttpErrorMessage(error);
    } else if (error instanceof Error) {
      message = error.message;
    } else {
      message = error;
    }

    if (showError) {
      console.error('Error occurred:', error);
    }

    this.showErrorSnackbar(message, action, duration);
  }

  handleSuccess(message: string, config: ErrorConfig = {}): void {
    const { duration = 3000, action = 'OK' } = config;

    this.snackBar.open(message, action, {
      duration,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['success-snackbar']
    });
  }

  handleInfo(message: string, config: ErrorConfig = {}): void {
    const { duration = 3000, action = 'OK' } = config;

    this.snackBar.open(message, action, {
      duration,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['info-snackbar']
    });
  }

  handleWarning(message: string, config: ErrorConfig = {}): void {
    const { duration = 4000, action = 'OK' } = config;

    this.snackBar.open(message, action, {
      duration,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['warning-snackbar']
    });
  }

  private getHttpErrorMessage(error: HttpErrorResponse): string {
    if (error.error instanceof ErrorEvent) {
      return `Erro de rede: ${error.error.message}`;
    }

    switch (error.status) {
      case 0:
        return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
      case 400:
        return error.error?.message || 'Requisição inválida.';
      case 401:
        return 'Sessão expirada. Faça login novamente.';
      case 403:
        return 'Você não tem permissão para realizar esta ação.';
      case 404:
        return 'Recurso não encontrado.';
      case 409:
        return error.error?.message || 'Conflito ao processar requisição.';
      case 500:
        return 'Erro interno do servidor. Tente novamente mais tarde.';
      case 503:
        return 'Serviço temporariamente indisponível.';
      default:
        return error.error?.message || `Erro: ${error.status} - ${error.statusText}`;
    }
  }

  private showErrorSnackbar(message: string, action: string, duration: number): void {
    this.snackBar.open(message, action, {
      duration,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['error-snackbar']
    });
  }
}
