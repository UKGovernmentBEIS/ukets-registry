import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { cancelNotificationRequest } from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-cancel-notification-request-container',
  template: `<app-cancel-update-request
    [updateRequestText]="'notification'"
    [pageTitle]="'notifications list'"
    (cancelRequest)="onCancel()"
  ></app-cancel-update-request> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelNotificationRequestContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelNotificationRequest());
  }
}
