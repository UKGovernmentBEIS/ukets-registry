import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { MainWizardRoutes } from '../../../main-wizard.routes';
import { AccountHolderWizardRoutes } from '../../account-holder-wizard-properties';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAccountHolder,
  selectAccountHolderExisting,
  selectAccountHolderType,
  selectAccountHolderWizardCompleted,
} from '../../account-holder.selector';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { completeWizard, nextPage } from '../../account-holder.actions';
import { take } from 'rxjs/operators';
import {
  AccountHolder,
  AccountHolderType,
  Organisation,
} from '@shared/model/account/account-holder';

@Component({
  selector: 'app-organisation-details-container',
  template: `
    <app-account-holder-organisation-details
      [caption]="'Add the account holder'"
      [header]="'Add the organisation details'"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-organisation-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OrganisationDetailsContainerComponent implements OnInit {
  accountHolder$: Observable<Organisation> = this.store.select(
    selectAccountHolder
  ) as Observable<Organisation>;
  accountHolderType$: Observable<string> = this.store.select(
    selectAccountHolderType
  );
  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectAccountHolderExisting
  );
  accountHolderCompleted$: Observable<boolean> = this.store.select(
    selectAccountHolderWizardCompleted
  );

  readonly noAccountHolderTypeRoute = MainWizardRoutes.ACCOUNT_TYPE;
  readonly wrongAccountHolderTypeRoute =
    AccountHolderWizardRoutes.INDIVIDUAL_DETAILS;
  readonly previousRoute = AccountHolderWizardRoutes.ACCOUNT_HOLDER_SELECTION;
  readonly nextRoute = AccountHolderWizardRoutes.ORGANISATION_ADDRESS_DETAILS;
  readonly overviewRoute = AccountHolderWizardRoutes.OVERVIEW;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    // this.accountHolderType$ = this.store.select(selectAccountHolderType);
    this.accountHolderType$.pipe(take(1)).subscribe((accountHolderType) => {
      if (!accountHolderType) {
        this._router.navigate([this.noAccountHolderTypeRoute], {
          skipLocationChange: true,
        });
      } else if (accountHolderType === AccountHolderType.INDIVIDUAL) {
        this._router.navigate([this.wrongAccountHolderTypeRoute], {
          skipLocationChange: true,
        });
      }
    });

    this.accountHolderExisting$.pipe(take(1)).subscribe((existing) => {
      if (existing) {
        this._router.navigate([this.overviewRoute], {
          skipLocationChange: true,
        });
      }
    });
    this.accountHolderCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.overviewRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.previousRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
    this.store.dispatch(completeWizard({ complete: false }));
  }

  onContinue(value: AccountHolder) {
    this.store.dispatch(
      nextPage({
        accountHolder: value,
      })
    );
    this._router.navigate([this.nextRoute], { skipLocationChange: true });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
