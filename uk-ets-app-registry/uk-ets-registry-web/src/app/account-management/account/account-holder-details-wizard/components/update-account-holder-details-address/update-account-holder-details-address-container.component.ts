import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AccountHolder, AccountHolderType } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectCountryCodes
} from '@shared/shared.selector';
import { selectUpdatedAccountHolder } from '../../reducers/account-holder-details.selector';
import {
  cancelClicked,
  setAccountHolderAddress
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';

@Component({
  selector: 'app-update-account-holder-details-address-container',
  template: `
    <app-account-holder-individual-contact-details
      [isAHUpdateWizard]="true"
      *ngIf="(accountHolder$ | async)?.type === individual"
      [caption]="'Request to update the account holder'"
      [header]="'Update the account holder details'"
      [accountHolder]="accountHolder$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (errorDetails)="onError($event)"
      (output)="onContinue($event)"
    ></app-account-holder-individual-contact-details>
    <app-account-holder-organisation-address
      [isAHUpdateWizard]="true"
      *ngIf="(accountHolder$ | async)?.type === organisation"
      [caption]="'Request to update the account holder'"
      [header]="'Update the account holder details'"
      [countries]="countries$ | async"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-organisation-address>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UpdateAccountHolderDetailsAddressContainerComponent
  implements OnInit {
  countries$: Observable<IUkOfficialCountry[]>;
  accountHolder$: Observable<AccountHolder>;
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  >;

  organisation = AccountHolderType.ORGANISATION;
  individual = AccountHolderType.INDIVIDUAL;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountHolderDetailsWizardPathsModel.BASE_PATH}/${
          AccountHolderDetailsWizardPathsModel.UPDATE_ACCOUNT_HOLDER
        }`
      })
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.accountHolder$ = this.store.select(selectUpdatedAccountHolder);
    this.countryCodes$ = this.store.select(selectCountryCodes);
  }

  onContinue(value: AccountHolder) {
    this.store.dispatch(
      setAccountHolderAddress({
        accountHolderInfoChanged: {
          address: value.address,
          emailAddress: value['emailAddress'],
          phoneNumber: value['phoneNumber']
        }
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
