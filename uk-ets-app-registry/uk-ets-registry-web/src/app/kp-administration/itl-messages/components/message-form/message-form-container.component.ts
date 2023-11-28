import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { sendMessage } from '@kp-administration/store';

@Component({
  selector: 'app-message-form-container',
  template: `
    <app-message-form
      (sendMessageRequest)="onSubmitSendMessageRequest($event)"
      (errorDetails)="onError($event)"
    >
    </app-message-form>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MessageFormContainerComponent {
  constructor(private store: Store) {}

  onSubmitSendMessageRequest(request: { content: string }) {
    this.store.dispatch(
      sendMessage({
        content: request.content,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
