import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import {
  clearCurrentAuthorisedRepresentative,
  fetchAuthorisedRepresentative,
  fetchAuthorisedRepresentatives,
} from '@account-opening/authorised-representative/authorised-representative.actions';
import { Observable } from 'rxjs';
import { AccountHolder, AuthorisedRepresentative } from '@shared/model/account';
import { selectAccountHolder } from '@account-opening/account-holder/account-holder.selector';
import { AuthModel } from '../../../auth/auth.model';
import { selectLoggedInUser } from '../../../auth/auth.selector';
import {
  selectAuthorisedRepresentatives,
  selectFetchedAuthorisedRepresentatives,
} from '@account-opening/authorised-representative/authorised-representative.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { take } from 'rxjs/operators';
import { selectErrorSummary } from '@shared/shared.selector';

@Component({
  selector: 'app-selection-container',
  template: `
    <app-selection
      [accountHolder]="accountHolder$ | async"
      [loggedInUser]="loggedInUser$ | async"
      [authorisedRepresentatives]="authorisedRepresentatives$ | async"
      [fetchedAuthorisedRepresentatives]="
        fetchedAuthorisedRepresentatives$ | async
      "
      (output)="onContinue($event)"
      [errorSummary]="errorSummary$ | async"
      (errorDetails)="onError($event)"
    ></app-selection>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectionContainerComponent implements OnInit {
  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;

  accountHolder$: Observable<AccountHolder>;

  loggedInUser$: Observable<AuthModel>;

  fetchedAuthorisedRepresentatives$: Observable<AuthorisedRepresentative[]>;

  authorisedRepresentatives$: Observable<AuthorisedRepresentative[]>;
  errorSummary$: Observable<ErrorSummary>;

  constructor(
    private _router: Router,
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.mainWizardRoute,
        extras: { skipLocationChange: true },
      })
    );
    this.store.dispatch(clearCurrentAuthorisedRepresentative());
    this.accountHolder$ = this.store.select(selectAccountHolder);
    this.loggedInUser$ = this.store.select(selectLoggedInUser);
    this.fetchedAuthorisedRepresentatives$ = this.store.select(
      selectFetchedAuthorisedRepresentatives
    );
    this.authorisedRepresentatives$ = this.store.select(
      selectAuthorisedRepresentatives
    );

    this.accountHolder$.pipe(take(1)).subscribe((accountHolder) => {
      this.store.dispatch(
        fetchAuthorisedRepresentatives({
          accountHolderId: accountHolder?.id?.toString(),
          errorSummary: this.getErrorSummary(
            'Sorry, there is a problem with the service. You might not be able to select an authorised representative.'
          ),
        })
      );
    });
    this.errorSummary$ = this.store.select(selectErrorSummary);
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onContinue(value: string) {
    this.store.dispatch(
      fetchAuthorisedRepresentative({
        urid: value,
        errorSummaries: [this.getErrorSummary('Enter a valid User ID.')],
      })
    );
  }

  getErrorSummary(message): ErrorSummary {
    return new ErrorSummary([new ErrorDetail(null, message)]);
  }
}
