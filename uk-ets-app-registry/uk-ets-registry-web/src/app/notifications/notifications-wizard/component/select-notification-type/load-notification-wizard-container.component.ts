import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { selectNotificationId } from '@notifications/notifications-wizard/reducers';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-load-notification-wizard-container',
  template: ` <app-select-notification-type-container
      *ngIf="(notificationId$ | async) == null"
    ></app-select-notification-type-container
    ><app-notifications-scheduled-date-container
      *ngIf="notificationId$ | async"
    ></app-notifications-scheduled-date-container>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoadNotificationWizardContainerComponent implements OnInit {
  notificationId$: Observable<string>;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.notificationId$ = this.store.select(selectNotificationId);
  }
}
