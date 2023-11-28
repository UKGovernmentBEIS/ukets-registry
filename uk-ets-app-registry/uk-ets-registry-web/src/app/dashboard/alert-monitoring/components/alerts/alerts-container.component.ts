import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { Observable, combineLatest, of } from 'rxjs';
import { selectAlerts } from '@registry-web/dashboard/alert-monitoring/reducers/alerts.selectors';
import { AlertsModel } from '@registry-web/dashboard/alert-monitoring/model/alerts.model';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { concatMap, tap } from 'rxjs/operators';
import { getAlerts } from '@registry-web/dashboard/alert-monitoring/actions';

@Component({
  selector: 'app-alerts-container',
  template: `<app-alerts [alerts]="alerts$ | async"></app-alerts>`,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlertsContainerComponent implements OnInit {
  alerts$: Observable<[AlertsModel, boolean]>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.alerts$ = this.getAlertsDetails();
  }

  private getAlertsDetails(): Observable<[AlertsModel, boolean]> {
    const alerts$ = this.store.select(selectAlerts);
    const isAdmin$ = this.store.select(isAdmin);

    return combineLatest([isAdmin$, alerts$]).pipe(
      concatMap(([isAdmin, alerts]) => {
        if (!alerts && isAdmin) {
          this.store.dispatch(getAlerts());
        }
        const resultObs: [AlertsModel, boolean] = [alerts, isAdmin];
        return of(resultObs);
      })
    );
  }
}
