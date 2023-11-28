import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthorisedRepresentative } from '@shared/model/account';
import { Store } from '@ngrx/store';
import {
  selectAuthorisedRepresentativesOtherAccounts,
  selectEligibleArsAsOptions
} from '@authorised-representatives/reducers';
import { ActivatedRoute, Router } from '@angular/router';
import {
  cancelClicked,
  fetchAuthorisedRepresentativesOtherAccounts,
  replaceAuthorisedRepresentative
} from '@authorised-representatives/actions/authorised-representatives.actions';
import { canGoBack, errors } from '@shared/shared.action';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { ArUpdateRequest } from '@authorised-representatives/model/ar-update-request';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-replace-representative-container',
  template: `
    <app-replace-representative
      [currentAuthorisedRepresentatives]="
        currentAuthorisedRepresentatives$ | async
      "
      [authorisedRepresentativesOtherAccounts]="
        authorisedRepresentativesOtherAccounts$ | async
      "
      (replaceAuthorisedRepresentative)="
        onReplaceAuthorisedRepresentative($event)
      "
      (errorDetails)="onError($event)"
    ></app-replace-representative>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReplaceRepresentativeContainerComponent implements OnInit {
  currentAuthorisedRepresentatives$: Observable<Option[]>;
  authorisedRepresentativesOtherAccounts$: Observable<
    AuthorisedRepresentative[]
  >;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(fetchAuthorisedRepresentativesOtherAccounts());

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}/authorised-representatives/select-update-type`
      })
    );
    this.authorisedRepresentativesOtherAccounts$ = this.store.select(
      selectAuthorisedRepresentativesOtherAccounts
    );

    this.currentAuthorisedRepresentatives$ = this.store.select(
      selectEligibleArsAsOptions
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.activatedRoute.snapshot['_routerState'].url })
    );
  }

  onReplaceAuthorisedRepresentative(updateRequest: ArUpdateRequest) {
    this.store.dispatch(replaceAuthorisedRepresentative({ updateRequest }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
