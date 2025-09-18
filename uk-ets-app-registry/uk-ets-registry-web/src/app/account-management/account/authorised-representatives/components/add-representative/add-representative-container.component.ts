import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthorisedRepresentative } from '@shared/model/account';
import { Store } from '@ngrx/store';
import { selectAuthorisedRepresentativesOtherAccounts } from '@authorised-representatives/reducers';
import {
  cancelClicked,
  fetchAuthorisedRepresentativesOtherAccounts,
  selectAuthorisedRepresentative,
} from '@authorised-representatives/actions/authorised-representatives.actions';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { selectErrorSummary } from '@shared/shared.selector';

@Component({
  selector: 'app-add-representative-container',
  template: `
    <app-add-representative
      [authorisedRepresentatives]="authorisedRepresentatives$ | async"
      (selectAuthorizedRepresentative)="
        onAuthorizedRepresentativeSelected($event)
      "
      (errorDetails)="onError($event)"
      [errorSummary]="errorSummary$ | async"
    ></app-add-representative>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddRepresentativeContainerComponent implements OnInit {
  authorisedRepresentatives$: Observable<AuthorisedRepresentative[]>;
  errorSummary$: Observable<ErrorSummary>;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(fetchAuthorisedRepresentativesOtherAccounts());

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}/authorised-representatives/select-update-type`,
      })
    );

    this.authorisedRepresentatives$ = this.store.select(
      selectAuthorisedRepresentativesOtherAccounts
    );

    this.errorSummary$ = this.store.select(selectErrorSummary);
  }

  onAuthorizedRepresentativeSelected(urid: string) {
    this.store.dispatch(selectAuthorisedRepresentative({ urid }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.activatedRoute.snapshot['_routerState'].url })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
