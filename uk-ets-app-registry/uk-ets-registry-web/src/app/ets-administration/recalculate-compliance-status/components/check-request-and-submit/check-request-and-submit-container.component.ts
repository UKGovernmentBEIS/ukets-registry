import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { startRecalculatioComplianceStatuAllCompliantEntities } from '@recalculate-compliance-status/store/actions';

@Component({
  selector: 'app-check-request-and-submit-container',
  template: `
    <app-check-request-and-submit
      (startRecalculationComplianceStatusClick)="
        startRecalculatioComplianceStatuAllCompliantEntities()
      "
    >
    </app-check-request-and-submit>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckRequestAndSubmitContainerComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: '/ets-administration/recalculate-compliance-status',
        extras: { skipLocationChange: true },
      })
    );
  }

  startRecalculatioComplianceStatuAllCompliantEntities(): void {
    this.store.dispatch(startRecalculatioComplianceStatuAllCompliantEntities());
  }
}
