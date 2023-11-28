import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  selectAccountHolderContactChanged,
  selectAccountHolderInfoChanged,
  selectContactType,
  selectCurrentAccountHolder,
  selectCurrentAccountHolderContact,
  selectUpdateType,
} from '@account-management/account/account-holder-details-wizard/reducers/account-holder-details.selector';
import { Observable } from 'rxjs';
import { AccountHolder, AccountHolderContact } from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  submitUpdateRequest,
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import {
  AccountHolderContactChanged,
  AccountHolderDetailsType,
  AccountHolderDetailsWizardPathsModel,
  AccountHolderInfoChanged,
  AccountHolderUpdate,
} from '@account-management/account/account-holder-details-wizard/model';
import { AccountHolderDetailsWizardActions } from '@account-management/account/account-holder-details-wizard/actions';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';
import { selectAllCountries } from '@registry-web/shared/shared.selector';

@Component({
  selector: 'app-check-update-request-container',
  template: `
    <app-check-update-request
      [caption]="'Request to update the account holder'"
      [header]="'Check your answers'"
      [accountHolder]="accountHolder$ | async"
      [accountHolderInfoChanged]="accountHolderInfoChanged$ | async"
      [accountHolderContact]="accountHolderContact$ | async"
      [accountHolderContactChanged]="accountHolderContactChanged$ | async"
      [contactType]="selectContactType$ | async"
      [updateType]="updateType$ | async"
      [accountIdentifier]="accountIdentifier$ | async"
      [countries]="countries$ | async"
      (clickChange)="navigateTo($event)"
      (submitRequest)="onSubmit($event)"
      (errorDetails)="onError($event)"
    ></app-check-update-request>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateRequestContainerComponent implements OnInit {
  accountHolder$: Observable<AccountHolder>;
  accountHolderInfoChanged$: Observable<AccountHolderInfoChanged>;
  accountHolderContact$: Observable<AccountHolderContact>;
  accountHolderContactChanged$: Observable<AccountHolderContactChanged>;
  updateType$: Observable<AccountHolderDetailsType>;
  accountIdentifier$: Observable<string>;
  selectContactType$: Observable<ContactType>;
  countries$: Observable<IUkOfficialCountry[]>;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.updateType$ = this.store.select(selectUpdateType);
    this.accountIdentifier$ = this.store.select(selectAccountId);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountHolderDetailsWizardPathsModel.BASE_PATH}/${
          this.route.snapshot.data.goBackPath
        }`,
        extras: { skipLocationChange: true },
      })
    );
    this.accountHolder$ = this.store.select(selectCurrentAccountHolder);
    this.accountHolderInfoChanged$ = this.store.select(
      selectAccountHolderInfoChanged
    );
    this.accountHolderContact$ = this.store.select(
      selectCurrentAccountHolderContact
    );
    this.accountHolderContactChanged$ = this.store.select(
      selectAccountHolderContactChanged
    );
    this.selectContactType$ = this.store.select(selectContactType);
    this.countries$ = this.store.select(selectAllCountries);
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onSubmit(submittedValues: AccountHolderUpdate) {
    this.store.dispatch(
      submitUpdateRequest({ accountHolderUpdateValues: submittedValues })
    );
  }

  navigateTo(routePath: AccountHolderDetailsWizardPathsModel) {
    this.store.dispatch(
      AccountHolderDetailsWizardActions.navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get('accountId')}/${
          AccountHolderDetailsWizardPathsModel.BASE_PATH
        }/${routePath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
