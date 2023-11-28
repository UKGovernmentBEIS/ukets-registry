import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { setNotificationsDetails } from '@notifications/notifications-details/actions/notification-details.action';
import { Injectable } from '@angular/core';

@Injectable()
export class NotificationDetailsGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(
      setNotificationsDetails({
        notificationId: route.paramMap.get('notificationId'),
      })
    );
    return true;
  }
}
