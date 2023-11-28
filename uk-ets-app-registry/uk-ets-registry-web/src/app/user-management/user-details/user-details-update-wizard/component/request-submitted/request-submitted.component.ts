import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UserUpdateDetailsType } from '@user-update/model';

@Component({
  selector: 'app-request-user-details-update-submitted',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Request submitted page'"
    ></div>
    <app-request-submitted
      [submittedIdentifier]="submittedIdentifier"
      [urid]="urid"
      [isMyProfilePage]="isMyProfilePage"
      [showOnlyAdminMessage]="true"
      [toBeLoggedOut]="toBeLoggedOff"
      (navigateToEmitter)="navigateTo()"
    ></app-request-submitted> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestSubmittedComponent implements OnInit {
  @Input()
  submittedIdentifier: string;
  @Input()
  urid: string;
  @Input()
  isMyProfilePage: boolean;
  @Input()
  updateType: UserUpdateDetailsType;
  @Output()
  readonly navigateToEmitter = new EventEmitter<string>();

  ngOnInit(): void {
    if (this.toBeLoggedOff) {
      setTimeout(() => {
        this.navigateToEmitter.emit();
      }, 2500);
    }
  }

  get toBeLoggedOff() {
    return (
      this.isMyProfilePage &&
      this.updateType === UserUpdateDetailsType.DEACTIVATE_USER
    );
  }

  navigateTo() {
    this.navigateToEmitter.emit();
  }
}
