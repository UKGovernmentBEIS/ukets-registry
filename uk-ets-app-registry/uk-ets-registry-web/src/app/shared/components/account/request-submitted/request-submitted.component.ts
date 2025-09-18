import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-request-submitted',
  templateUrl: './request-submitted.component.html',
})
export class RequestSubmittedComponent implements OnInit {
  @Input()
  confirmationMessageTitle: string;

  @Input()
  confirmationMessageBody: string;

  @Input()
  submittedIdentifier: string;

  @Input()
  customWhatHappensNext: string;

  @Input()
  showOnlyAdminMessage = false;

  @Input()
  accountId: string;

  @Input()
  notificationId: string;

  @Input()
  urid: string;

  @Input()
  isMyProfilePage: boolean;

  @Input()
  isAdmin: boolean;

  @Input()
  toBeLoggedOut: boolean;

  @Output()
  readonly navigateToEmitter = new EventEmitter<string>();

  path: string;
  goBack: string;

  constructor(private router: Router) {}

  ngOnInit(): void {
    if (this.accountId) {
      this.path = '/account/' + this.accountId;
      this.goBack = 'Go back to the account';
    }
    if (this.urid) {
      if (this.isMyProfilePage) {
        this.path = '/user-details/my-profile';
      } else {
        this.path = '/user-details/' + this.urid;
      }
      this.goBack = 'Back to the user details page';
    }
    if (this.notificationId) {
      this.path = '/notifications';
      this.goBack = 'Go back to notifications list';
    }
  }

  navigateTo(): void {
    this.navigateToEmitter.emit();
  }

  navigateToNotifications() {
    if (this.path === '/notifications') {
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate([this.path]);
      });
    }
  }
}
