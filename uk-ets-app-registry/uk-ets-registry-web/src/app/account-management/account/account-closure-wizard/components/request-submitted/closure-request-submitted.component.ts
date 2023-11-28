import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-account-closure-request-submitted',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Request submitted page'"
    ></div>
    <app-request-submitted
      [confirmationMessageTitle]="'Close request has been submitted'"
      [submittedIdentifier]="submittedIdentifier"
      [accountId]="accountId"
      [showOnlyAdminMessage]="true"
    ></app-request-submitted> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClosureRequestSubmittedComponent {
  @Input()
  submittedIdentifier: string;
  @Input()
  accountId: string;
}
