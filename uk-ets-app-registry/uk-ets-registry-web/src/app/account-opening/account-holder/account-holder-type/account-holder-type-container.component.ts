import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { setAccountHolderType } from '../account-holder.actions';
import { Observable } from 'rxjs';
import * as MainWizardProperties from '../../main-wizard.routes';
import { selectAccountType } from '../../account-opening.selector';
import {
  selectAccountHolderType,
  selectAccountHolderWizardCompleted,
} from '../account-holder.selector';
import { AccountHolderWizardRoutes } from '../account-holder-wizard-properties';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { AccountHolderType } from '@shared/model/account/account-holder';
import { AccountType } from '@shared/model/account';

@Component({
  selector: 'app-account-holder-type-container',
  template: `
    <app-account-holder-type
      [caption]="'Add the account holder'"
      [accountHolderType]="accountHolderType$ | async"
      (selectedAccountHolderType)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-type>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountHolderTypeContainerComponent implements OnInit {
  accountType$: Observable<AccountType>;
  accountHolderCompleted$: Observable<boolean>;
  accountHolderType$: Observable<AccountHolderType>;

  readonly mainWizardRoute = MainWizardProperties.MainWizardRoutes.TASK_LIST;
  readonly noAccountTypeRouter =
    MainWizardProperties.MainWizardRoutes.ACCOUNT_TYPE;
  readonly nextRoute = AccountHolderWizardRoutes.ACCOUNT_HOLDER_SELECTION;
  readonly overviewRoute = AccountHolderWizardRoutes.OVERVIEW;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}
  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.mainWizardRoute,
        extras: { skipLocationChange: true },
      })
    );

    this.accountType$ = this.store.select(selectAccountType);
    this.accountHolderCompleted$ = this.store.select(
      selectAccountHolderWizardCompleted
    );
    this.accountHolderType$ = this.store.select(selectAccountHolderType);

    this.accountType$.pipe(take(1)).subscribe((accountType) => {
      if (accountType === null) {
        this._router.navigate([this.noAccountTypeRouter], {
          skipLocationChange: true,
        });
      }
    });
    this.accountHolderCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this._router.navigate([this.overviewRoute], {
          skipLocationChange: true,
        });
      }
    });
  }

  onContinue(value: AccountHolderType) {
    this.store.dispatch(setAccountHolderType({ holderType: value }));

    this._router.navigate([this.nextRoute], {
      skipLocationChange: true,
    });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
