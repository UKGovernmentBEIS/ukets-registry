import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';

@Component({
  selector: 'app-request-minor-user-details-update-submitted',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Request submitted page'"
    ></div>
    <app-request-submitted
      [urid]="urid"
      [isMyProfilePage]="isMyProfilePage"
      [confirmationMessageTitle]="'The user details have been updated'"
      [toBeLoggedOut]="false"
      (navigateToEmitter)="navigateTo()"
    ></app-request-submitted> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestSubmittedForMinorChangesComponent {
  @Input()
  urid: string;
  @Input()
  isMyProfilePage: boolean;
  @Output()
  readonly navigateToEmitter = new EventEmitter<string>();

  navigateTo() {
    this.navigateToEmitter.emit();
  }
}
