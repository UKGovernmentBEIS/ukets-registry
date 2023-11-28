import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { selectAccountType } from '../../account-opening.selector';
import { AccountDetails } from '@shared/model/account/account-details';
import {
  selectAccountDetails,
  selectAccountDetailsCompleted,
  selectAccountDetailsCountry,
} from '../account-details.selector';
import { AccountDetailsWizardRoutes } from '../account-details-wizard-properties';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import {
  completeWizard,
  deleteAccountDetails,
} from '../account-details.actions';
import { AccountType, AccountTypeMap } from '@shared/model/account';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
})
export class OverviewComponent implements OnInit {
  accountType$: Observable<AccountType> = this.store.select(selectAccountType);
  accountDetails$: Observable<AccountDetails> = this.store.select(
    selectAccountDetails
  ) as Observable<AccountDetails>;
  accountDetailsCountry$: Observable<string> = this.store.select(
    selectAccountDetailsCountry
  ) as Observable<string>;
  accountDetailsCompleted$: Observable<boolean> = this.store.select(
    selectAccountDetailsCompleted
  ) as Observable<boolean>;
  accountDetailsWizardRoutes = AccountDetailsWizardRoutes;
  accountTypeText: string;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly accountDetailsRoute = AccountDetailsWizardRoutes.ACCOUNT_DETAILS;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.accountDetailsCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.mainWizardRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.accountDetailsRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
    this.accountType$.pipe(take(1)).subscribe((accountType) => {
      this.accountTypeText = AccountTypeMap[accountType].label;
    });
  }

  onEdit() {
    this._router.navigate([this.accountDetailsRoute], {
      skipLocationChange: true,
    });
  }

  onApply() {
    this.store.dispatch(completeWizard({ complete: true }));
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  onDelete() {
    this.store.dispatch(deleteAccountDetails());
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }
}
