import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import {
  setNotificationsInfo,
  setNotificationsRequest,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { Injectable } from '@angular/core';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { selectCurrentNotification } from '@notifications/notifications-wizard/reducers';
import {
  Notification,
  NotificationType,
  NotificationsWizardPathsModel,
} from '@notifications/notifications-wizard/model';

@Injectable({
  providedIn: 'root',
})
export class NotificationRequestGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    snapshot: RouterStateSnapshot
  ): Observable<boolean> {
    let notificationRequest: NotificationRequestEnum;
    if (!route.paramMap.get('notificationId')) {
      notificationRequest = NotificationRequestEnum.NEW;
    } else {
      if (snapshot.url.endsWith(NotificationsWizardPathsModel.CLONE)) {
        notificationRequest = NotificationRequestEnum.CLONE;
      } else {
        notificationRequest = NotificationRequestEnum.UPDATE;
      }
    }

    this.store.dispatch(
      setNotificationsRequest({
        notificationRequest: notificationRequest,
        notificationId: route.paramMap.get('notificationId'),
      })
    );
    if (route.paramMap.get('notificationId')) {
      return this.getNotificationDetailsInfo(
        route.paramMap.get('notificationId')
      ).pipe(
        switchMap((notification) => {
          // Do not allow cloning for non `AD_HOC` and not `EXPIRED` notifications
          if (
            snapshot.url.endsWith(NotificationsWizardPathsModel.CLONE) &&
            notification.type !== NotificationType.AD_HOC &&
            notification.status !== 'EXPIRED'
          ) {
            return of(false);
          }
          return of(true);
        }),
        catchError(() => of(false))
      );
    } else {
      return of(true);
    }
  }

  private getNotificationDetailsInfo(notificationId: string) {
    return this.store.select(selectCurrentNotification).pipe(
      tap((data) => this.prefetch(notificationId, data)),
      filter((data) => data != null),
      take(1)
    );
  }

  private prefetch(notificationId: string, notification: Notification) {
    if (notification == null) {
      this.store.dispatch(
        setNotificationsInfo({
          notificationId,
        })
      );
    }
  }
}
