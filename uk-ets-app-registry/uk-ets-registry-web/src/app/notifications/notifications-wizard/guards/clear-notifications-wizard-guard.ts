import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { clearNotificationsRequest } from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { searchNotifications } from '@notifications/notifications-list/actions/notifications-list.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearNotificationsWizardGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearNotificationsRequest());
    this.store.dispatch(
      searchNotifications({
        criteria: { type: undefined },
        pageParameters: { page: 0, pageSize: 10 },
        sortParameters: {
          sortField: 'scheduledDate',
          sortDirection: 'DESC',
        },
        potentialErrors: null,
      })
    );
    return true;
  }
}
