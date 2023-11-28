import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { RecalculateComplianceRequestStatus } from '@recalculate-compliance-status/model';
import { selectRecalculateComplianceRequestStatus } from '@recalculate-compliance-status/store/reducers';
import { clearGoBackRoute, navigateTo } from '@shared/shared.action';

@Component({
  selector: 'app-recalculate-start-container',
  template: `
    <app-recalculate-start
      [recalculateComplianceRequestStatus]="
        recalculateComplianceRequestStatus$ | async
      "
      (recalculateClick)="onContinue()"
    >
    </app-recalculate-start>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecalculateStartContainerComponent implements OnInit {
  recalculateComplianceRequestStatus$: Observable<RecalculateComplianceRequestStatus>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(clearGoBackRoute());
    this.recalculateComplianceRequestStatus$ = this.store.select(
      selectRecalculateComplianceRequestStatus
    );
  }

  onContinue(): void {
    this.store.dispatch(
      navigateTo({
        route: `/ets-administration/recalculate-compliance-status/confirm`,
        extras: {
          skipLocationChange: true,
        },
      })
    );
  }
}
