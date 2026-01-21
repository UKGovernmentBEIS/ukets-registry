import {
  ChangeDetectionStrategy,
  Component,
  computed,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { selectCurrentAccountEmissionDetails } from '@exclusion-status-update-wizard/reducers';
import {
  cancelClicked,
  setExclusionYear,
} from '@registry-web/account-management/account/exclusion-status-update-wizard/actions/update-exclusion-status.action';
import { selectGroupedAllocationOverview } from '@account-management/account/account-details/account.selector';
import { toSignal } from '@angular/core/rxjs-interop';
import { AnnualAllocation } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-select-year-container',
  template: `
    <app-select-year
      *ngIf="emissionEntries$ | async as emissionEntries"
      [emissionEntries]="emissionEntries"
      [annuals]="annuals()"
      (errorDetails)="onError($event)"
      (selectYear)="onContinue($event)"
      (cancelEmitter)="onCancel()"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectYearContainerComponent implements OnInit {
  readonly emissionEntries$ = this.store.select(
    selectCurrentAccountEmissionDetails
  );

  private readonly groupedAllocationOverview = toSignal(
    this.store.select(selectGroupedAllocationOverview)
  );
  readonly annuals = computed<AnnualAllocation[]>(
    () =>
      this.groupedAllocationOverview()?.groupedAllocations?.map(
        (grouped) => grouped.summedAnnualAllocationStandardAndNer
      ) ?? []
  );

  constructor(
    private readonly store: Store,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
  }

  onContinue(value: number) {
    this.store.dispatch(setExclusionYear({ year: value }));
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: value };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(cancelClicked({ route: this.router.url }));
  }
}
