import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  setExclusionStatus,
} from '@registry-web/account-management/account/exclusion-status-update-wizard/actions/update-exclusion-status.action';
import {
  selectCurrentAccountEmissionDetails,
  selectExclusionYear,
} from '@exclusion-status-update-wizard/reducers';
import { UpdateExclusionStatusPathsModel } from '../model';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { VerifiedEmissions } from '@registry-web/account-shared/model';

@Component({
  selector: 'app-select-exclusion-status-container',
  template: `
    <app-select-exclusion-status
      [year]="year$ | async"
      [emissionEntries]="emissionEntries$ | async"
      (errorDetails)="onError($event)"
      (selectExclusionStatus)="onContinue($event)"
    ></app-select-exclusion-status>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectExclusionStatusContainerComponent implements OnInit {
  year$: Observable<number>;
  emissionEntries$: Observable<VerifiedEmissions[]>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${UpdateExclusionStatusPathsModel.BASE_PATH}`,
      })
    );
    this.emissionEntries$ = this.store.select(
      selectCurrentAccountEmissionDetails
    );
    this.year$ = this.store.select(selectExclusionYear);
  }

  onContinue(value: boolean) {
    this.store.dispatch(setExclusionStatus({ excluded: value }));
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
