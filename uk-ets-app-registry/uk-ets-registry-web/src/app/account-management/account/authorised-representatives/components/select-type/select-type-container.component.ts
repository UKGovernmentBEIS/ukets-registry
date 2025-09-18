import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { Store } from '@ngrx/store';
import { selectUpdateType } from '@authorised-representatives/reducers';
import { ActivatedRoute } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  setRequestUpdateType,
} from '@authorised-representatives/actions/authorised-representatives.actions';
import { FormRadioOption } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  selectAuthorisedRepresentatives,
  selectPendingARRequests,
} from '@account-management/account/account-details/account.selector';
import { selectConfigurationRegistry } from '@shared/shared.selector';
import { combineLatest, map, Observable } from 'rxjs';

@Component({
  selector: 'app-select-ar-update-type-container',
  template: `
    <app-select-ar-update-type
      [updateTypes]="updateTypes$ | async"
      [updateType]="updateType$ | async"
      [authorisedReps]="authorisedReps$ | async"
      [pendingARRequests]="pendingARRequests$ | async"
      [configuration]="configuration$ | async"
      (selectUpdateType)="onContinue($event)"
      (errorDetails)="onError($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectTypeContainerComponent implements OnInit {
  updateType$ = this.store.select(selectUpdateType);
  authorisedReps$ = this.store.select(selectAuthorisedRepresentatives);
  pendingARRequests$ = this.store.select(selectPendingARRequests);
  configuration$ = this.store.select(selectConfigurationRegistry);
  updateTypes$: Observable<FormRadioOption[]> = combineLatest([
    this.route.data,
    this.authorisedReps$,
  ]).pipe(
    map(([data, authorisedReps]) =>
      authorisedReps?.filter((ar) => ar.state !== 'REMOVED')?.length
        ? data.updateTypes
        : data.updateTypes.filter((updateType) => updateType.value === 'ADD')
    )
  );

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
  }

  onContinue(updateType: AuthorisedRepresentativesUpdateType) {
    this.store.dispatch(setRequestUpdateType({ updateType }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: value };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
