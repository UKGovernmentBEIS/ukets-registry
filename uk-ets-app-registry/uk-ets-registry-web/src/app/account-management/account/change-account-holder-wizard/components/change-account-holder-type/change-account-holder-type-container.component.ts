import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountHolderType } from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { setAccountHolderType } from '@change-account-holder-wizard/store/actions/change-account-holder-wizard.actions';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model/';
import { selectChangeAccountHolderType } from '@change-account-holder-wizard/store/reducers';

@Component({
  selector: 'app-change-account-holder-type-container',
  template: `
    <app-account-holder-type
      [accountHolderType]="accountHolderType$ | async"
      (selectedAccountHolderType)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-type>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAccountHolderTypeContainerComponent implements OnInit {
  accountHolderType$: Observable<AccountHolderType>;
  accountIdentifier$: Observable<string>;

  readonly nextRoute =
    ChangeAccountHolderWizardPathsModel.ACCOUNT_HOLDER_SELECTION;
  readonly overviewRoute =
    ChangeAccountHolderWizardPathsModel.CHECK_CHANGE_ACCOUNT_HOLDER;

  constructor(
    private route: ActivatedRoute,
    private store: Store
  ) {}
  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get('accountId')}`,
      })
    );

    this.accountHolderType$ = this.store.select(selectChangeAccountHolderType);
    this.accountIdentifier$ = this.store.select(selectAccountId);
  }

  onContinue(value: AccountHolderType) {
    this.store.dispatch(
      setAccountHolderType({
        holderType: value,
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
