import { Component, inject } from '@angular/core';
import { SharedModule } from '@registry-web/shared/shared.module';
import { selectSubmittedRequestIdentifier } from '@registry-web/claim-account/store';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-claim-account-submitted',
  template: `<app-request-submitted
    [confirmationMessageTitle]="'We have received your request'"
    [confirmationMessageBody]="'Your request ID'"
    [submittedIdentifier]="submittedIdentifier$ | async"
    [customWhatHappensNext]="
      'We have received your request to claim the Registry account. A Registry administrator will review this request and contact you to confirm any additional information or documents we may require from you to appoint you to the Registry account.'
    "
  />`,
  standalone: true,
  imports: [SharedModule],
})
export class ClaimAccountSubmittedComponent {
  private readonly store = inject(Store);
  readonly submittedIdentifier$ = this.store.select(
    selectSubmittedRequestIdentifier
  );
}
