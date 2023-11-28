import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import {
  AccountHolderDetailsType,
  AccountHolderDetailsWizardPathsModel,
} from '@account-management/account/account-holder-details-wizard/model';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { AccountHolderContact } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  cancelClicked,
  setAccountHolderContactWorkDetails,
  setSameAddressAlternativePrimaryContact,
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import {
  selectCurrentAccountHolderAddress,
  selectSameAlternativePrimaryContactAddress,
  selectUpdatedAlternativeAccountHolderContact,
  selectUpdateType,
} from '@account-management/account/account-holder-details-wizard/reducers';

@Component({
  selector:
    'app-update-account-holder-alternative-primary-contact-work-details-container',
  template: `
    <app-account-holder-contact-work-details
      [useUpdateLabel]="useUpdateLabel"
      [contactType]="contactType"
      [isAHUpdateWizard]="true"
      [accountHolderContact]="accountHolderContact$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (errorDetails)="onError($event)"
      [accountHolderAddress]="accountHolderAddress$ | async"
      [sameAddress]="sameAddress$ | async"
      (output)="onContinue($event)"
      (copyAccountHolderAddressToWorkAddress)="onAddressCheckChange($event)"
    ></app-account-holder-contact-work-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateAlternativePrimaryContactWorkDetailsContainerComponent
  implements OnInit {
  accountHolderAddress$: Observable<any>;
  countries$: Observable<IUkOfficialCountry[]>;
  accountHolderContact$: Observable<AccountHolderContact>;
  sameAddress$: Observable<boolean>;
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  >;
  contactType: string;
  useUpdateLabel = true;

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
          AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT
        }`,
      })
    );
    this.countryCodes$ = this.store.select(selectCountryCodes);
    this.countries$ = this.store.select(selectAllCountries);
    this.accountHolderAddress$ = this.store.select(
      selectCurrentAccountHolderAddress
    );
    this.sameAddress$ = this.store.select(
      selectSameAlternativePrimaryContactAddress
    );
    this.accountHolderContact$ = this.store.select(
      selectUpdatedAlternativeAccountHolderContact
    );
    this.store.select(selectUpdateType).subscribe((type) => {
      if (
        type ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD
      ) {
        this.useUpdateLabel = false;
      }
    });
    this.contactType = ContactType.ALTERNATIVE;
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      setAccountHolderContactWorkDetails({
        accountHolderContactChanged: {
          positionInCompany: value.positionInCompany,
          address: value.address,
          phoneNumber: value.phoneNumber,
          emailAddress: value.emailAddress,
        },
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onAddressCheckChange(event) {
    this.store.dispatch(
      setSameAddressAlternativePrimaryContact({ sameAddress: event })
    );
  }
}
