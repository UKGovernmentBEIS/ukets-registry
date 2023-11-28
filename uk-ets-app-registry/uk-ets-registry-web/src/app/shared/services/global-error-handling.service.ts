import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandlingService implements ErrorHandler {
  handleError(error: any): void {
    const chunkFailedError = /Loading chunk [\d]+ failed/;

    if (chunkFailedError.test(error.message)) {
      window.location.reload();
    }
    console.error(error);
  }
}
