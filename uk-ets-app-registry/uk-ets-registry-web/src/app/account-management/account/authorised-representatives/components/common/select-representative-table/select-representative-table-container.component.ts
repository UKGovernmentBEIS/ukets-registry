import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { Store } from '@ngrx/store';
import {
  selectEligibleArsForAction,
  selectSelectedArFromTableUrid,
  selectUpdateType
} from '@authorised-representatives/reducers';
import { ActivatedRoute } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  setSelectedArFromTable
} from '@authorised-representatives/actions/authorised-representatives.actions';
import { AuthorisedRepresentative } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AuthorisedRepresentativesRoutePaths } from '@authorised-representatives/model/authorised-representatives-route-paths.model';

@Component({
  selector: 'app-select-representative-table-container',
  template: `
    <app-select-representative-table
      [authorisedRepresentatives]="eligibleArsForAction$ | async"
      [updateType]="updateType$ | async"
      [selectedArFromTableUrid]="selectedArFromTableUrid$ | async"
      (selectAuthorisedRepresentative)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-representative-table>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectRepresentativeTableContainerComponent implements OnInit {
  updateType$: Observable<AuthorisedRepresentativesUpdateType>;
  eligibleArsForAction$: Observable<AuthorisedRepresentative[]>;
  selectedArFromTableUrid$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/authorised-representatives/${
          AuthorisedRepresentativesRoutePaths.SELECT_UPDATE_TYPE
        }`
      })
    );
    this.updateType$ = this.store.select(selectUpdateType);
    this.eligibleArsForAction$ = this.store.select(selectEligibleArsForAction);
    this.selectedArFromTableUrid$ = this.store.select(
      selectSelectedArFromTableUrid
    );
  }

  onContinue(selectedAr: string) {
    this.store.dispatch(setSelectedArFromTable({ selectedAr }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
