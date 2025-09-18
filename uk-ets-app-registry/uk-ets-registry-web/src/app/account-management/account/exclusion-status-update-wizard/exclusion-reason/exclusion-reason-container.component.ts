import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  setExclusionReason,
} from '@registry-web/account-management/account/exclusion-status-update-wizard/actions/update-exclusion-status.action';
import {
  selectCurrentAccountEmissionDetails,
  selectExclusionReason,
  selectExclusionStatus,
  selectExclusionYear,
} from '@exclusion-status-update-wizard/reducers';
import { UpdateExclusionStatusPathsModel } from '../model';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { VerifiedEmissions } from '@registry-web/account-shared/model';
import { ExclusionReasonComponent } from './exclusion-reason.component';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  imports: [ExclusionReasonComponent, SharedModule],
  selector: 'app-exclusion-reason-container',
  template: `
    <app-exclusion-reason
      [year]="year$ | async"
      [emissionEntries]="emissionEntries$ | async"
      [exclusionReason]="exclusionReason$ | async"
      (submitReason)="onContinue($event)"
    ></app-exclusion-reason>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExclusionReasonContainerComponent implements OnInit {
  year$: Observable<number>;
  emissionEntries$: Observable<VerifiedEmissions[]>;
  exclusionReason$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${UpdateExclusionStatusPathsModel.BASE_PATH}/${
          UpdateExclusionStatusPathsModel.SELECT_EXCLUSION_STATUS
        }`,
        extras: { skipLocationChange: true },
      })
    );
    this.emissionEntries$ = this.store.select(
      selectCurrentAccountEmissionDetails
    );
    this.year$ = this.store.select(selectExclusionYear);
    this.exclusionReason$ = this.store.select(selectExclusionReason);
  }

  onContinue(reason: string) {
    this.store.dispatch(setExclusionReason({ reason }));
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
