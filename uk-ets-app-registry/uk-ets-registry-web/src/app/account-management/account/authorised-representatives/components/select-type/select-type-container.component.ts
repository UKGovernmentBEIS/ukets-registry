import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { Store } from '@ngrx/store';
import { selectUpdateType } from '@authorised-representatives/reducers';
import { ActivatedRoute, Data } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  setRequestUpdateType,
} from '@authorised-representatives/actions/authorised-representatives.actions';
import { FormRadioOption } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  ArSubmittedUpdateRequest,
  AuthorisedRepresentative,
} from '@shared/model/account';
import {
  selectAuthorisedRepresentatives,
  selectPendingARRequests,
} from '@account-management/account/account-details/account.selector';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@shared/shared.selector';

@Component({
  selector: 'app-select-ar-update-type-container',
  template: `
    <app-select-ar-update-type
      [updateTypes]="updateTypes"
      [updateType]="updateType$ | async"
      [authorisedReps]="authorisedReps$ | async"
      [pendingARRequests]="pendingARRequests$ | async"
      [configuration]="configuration$ | async"
      (selectUpdateType)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-ar-update-type>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectTypeContainerComponent implements OnInit {
  updateType$: Observable<AuthorisedRepresentativesUpdateType>;
  updateTypes: FormRadioOption[];
  authorisedReps$: Observable<AuthorisedRepresentative[]>;
  pendingARRequests$: Observable<ArSubmittedUpdateRequest[]>;
  configuration$: Observable<Configuration[]>;

  constructor(private store: Store, private route: ActivatedRoute) {}
  ngOnInit() {
    this.configuration$ = this.store.select(selectConfigurationRegistry);

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.updateType$ = this.store.select(selectUpdateType);
    this.route.data.subscribe((data: Data) => {
      this.updateTypes = data.updateTypes;
    });

    this.authorisedReps$ = this.store.select(selectAuthorisedRepresentatives);

    this.pendingARRequests$ = this.store.select(selectPendingARRequests);
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
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
