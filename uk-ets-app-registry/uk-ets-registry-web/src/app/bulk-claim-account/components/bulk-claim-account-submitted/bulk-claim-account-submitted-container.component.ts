import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-bulk-claim-account-submitted-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Request submitted page'"
    ></div>
    <app-request-submitted
      [confirmationMessageTitle]="'The invitation(s) has been sent'"
      [showOnlyAdminMessage]="true"
    ></app-request-submitted> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BulkClaimAccountSubmittedContainerComponent {}
