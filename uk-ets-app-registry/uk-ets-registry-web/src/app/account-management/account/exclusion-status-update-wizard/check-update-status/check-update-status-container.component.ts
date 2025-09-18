import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  submitUpdate,
} from '@registry-web/account-management/account/exclusion-status-update-wizard/actions/update-exclusion-status.action';
import { UpdateExclusionStatusActions } from '../actions';
import {
  selectExclusionStatus,
  selectExclusionYear,
  selectExclusionReason,
  selectCurrentAccountEmissionDetails,
} from '@exclusion-status-update-wizard/reducers';
import {
  OperatorEmissionsExclusionStatus,
  UpdateExclusionStatusPathsModel,
} from '../model';
import { Account } from '@registry-web/shared/model/account';
import { selectAccount } from '../../account-details/account.selector';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { VerifiedEmissions } from '@registry-web/account-shared/model';

@Component({
  selector: 'app-check-update-status-container',
  template: `<app-check-update-status
      [year]="year$ | async"
      [exclusionStatus]="exclusionStatus$ | async"
      [exclusionReason]="exclusionReason$ | async"
      [emissions]="emissions$ | async"
      [account]="account$ | async"
      [routePathForYearSelection]="routePathForYearSelection"
      [routePathForExclusionStatus]="routePathForExclusionStatus"
      [routePathForExclusionReason]="routePathForExclusionReason"
      (navigateToEmitter)="navigateTo($event)"
      (cancelEmitter)="onCancel()"
      (submitUpdate)="onSubmit($event)"
      (errorDetails)="onError($event)"
    ></app-check-update-status>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateStatusContainerComponent implements OnInit {
  year$: Observable<number>;
  exclusionStatus$: Observable<boolean>;
  exclusionReason$: Observable<string>;
  emissions$: Observable<VerifiedEmissions[]>;
  account$: Observable<Account>;
  routePathForYearSelection: string;
  routePathForExclusionStatus: string;
  routePathForExclusionReason: string;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.routePathForYearSelection = UpdateExclusionStatusPathsModel.BASE_PATH;
    this.routePathForExclusionStatus =
      this.routePathForYearSelection +
      '/' +
      UpdateExclusionStatusPathsModel.SELECT_EXCLUSION_STATUS;
    this.routePathForExclusionReason =
      this.routePathForYearSelection +
      '/' +
      UpdateExclusionStatusPathsModel.EXCLUSION_REASON;
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${UpdateExclusionStatusPathsModel.BASE_PATH}/${
          UpdateExclusionStatusPathsModel.EXCLUSION_REASON
        }`,
        extras: { skipLocationChange: true },
      })
    );

    this.year$ = this.store.select(selectExclusionYear);
    this.exclusionStatus$ = this.store.select(selectExclusionStatus);
    this.exclusionReason$ = this.store.select(selectExclusionReason);
    this.emissions$ = this.store.select(selectCurrentAccountEmissionDetails);
    this.account$ = this.store.select(selectAccount);
  }

  navigateTo(routePath: string) {
    this.store.dispatch(
      UpdateExclusionStatusActions.navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${routePath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onSubmit(value: {
    accountIdentifier: string;
    exclusionStatus: OperatorEmissionsExclusionStatus;
  }) {
    this.store.dispatch(
      submitUpdate({
        accountIdentifier: value.accountIdentifier,
        exclusionStatus: value.exclusionStatus,
      })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
