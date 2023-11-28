import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Data } from '@angular/router';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { selectCurrentAccountEmissionDetails } from '@exclusion-status-update-wizard/reducers';
import { Observable } from 'rxjs';
import {
  cancelClicked,
  setExclusionYear,
} from '@registry-web/account-management/account/exclusion-status-update-wizard/actions/update-exclusion-status.action';
import { VerifiedEmissions } from '@registry-web/account-shared/model';

@Component({
  selector: 'app-select-year-container',
  template: `
    <app-select-year
      *ngIf="emissionEntries$ | async as emissionEntries"
      [emissionEntries]="emissionEntries"
      (errorDetails)="onError($event)"
      (selectYear)="onContinue($event)"
      (cancelEmitter)="onCancel()"
    ></app-select-year>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectYearContainerComponent implements OnInit {
  emissionEntries$: Observable<VerifiedEmissions[]>;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.emissionEntries$ = this.store.select(
      selectCurrentAccountEmissionDetails
    );
  }

  onContinue(value: number) {
    this.store.dispatch(setExclusionYear({ year: value }));
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
