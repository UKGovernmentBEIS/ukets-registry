import { Component, OnInit } from '@angular/core';
import { selectNotifications } from '@registry-web/dashboard/notifications/reducers';
import { Observable } from 'rxjs';
import { DashboardNotification } from '@registry-web/dashboard/notifications/model';
import { Store } from '@ngrx/store';
import { retrieveNotifications } from '@registry-web/dashboard/notifications/actions';

@Component({
  selector: 'app-dashboard-notifications-container',
  template: `<app-dashboard-notifications
    [notifications]="notifications$ | async"
  ></app-dashboard-notifications>`,
})
export class DashboardNotificationsContainerComponent implements OnInit {
  notifications$: Observable<DashboardNotification[]>;

  constructor(private store: Store) {
    this.notifications$ = this.store.select(selectNotifications);
  }

  ngOnInit(): void {
    this.store.dispatch(retrieveNotifications());
  }
}
