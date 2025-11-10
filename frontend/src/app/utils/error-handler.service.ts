import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

export interface ErrorHandlerOptions {
  showError?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  private snackBar = inject(MatSnackBar);

  handleError(error: string | Error | unknown, options: ErrorHandlerOptions = { showError: true }): void {
    let message = 'Ocorreu um erro inesperado.';

    if (typeof error === 'string') {
      message = error;
    } else if (error instanceof Error) {
      message = error.message || 'Ocorreu um erro inesperado.';
    } else if (error && typeof error === 'object' && 'error' in error) {
      const errorObj = error as { error?: { message?: string } };
      message = errorObj.error?.message || 'Ocorreu um erro inesperado.';
    }

    if (options.showError) {
      this.snackBar.open(message, 'Fechar', {
        duration: 5000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });
    }

    console.error('Error:', error);
  }

  handleSuccess(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['success-snackbar']
    });
  }

  handleWarning(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 4000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['warning-snackbar']
    });
  }
}
