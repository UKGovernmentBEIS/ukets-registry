import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountHolderContact } from '@shared/model/account';
import {
  cancelClicked,
  setAccountHolderContactWorkDetails,
  setSameAddressPrimaryContact,
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import {
  selectCurrentAccountHolderAddress,
  selectSamePrimaryContactAddress,
  selectUpdatedAccountHolderPrimaryContact,
} from '@account-management/account/account-holder-details-wizard/reducers';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-update-account-holder-primary-contact-work-details-container',
  template: `
    <app-account-holder-contact-work-details
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
export class UpdatePrimaryContactWorkDetailsContainerComponent
  implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact>;
  countries$: Observable<IUkOfficialCountry[]>;
  accountHolderAddress$: Observable<any>;
  sameAddress$: Observable<boolean>;
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  >;
  contactType: string;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.countryCodes$ = this.store.select(selectCountryCodes);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountHolderDetailsWizardPathsModel.BASE_PATH}/${
          AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT
        }`,
      })
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.accountHolderContact$ = this.store.select(
      selectUpdatedAccountHolderPrimaryContact
    );
    this.accountHolderAddress$ = this.store.select(
      selectCurrentAccountHolderAddress
    );
    this.sameAddress$ = this.store.select(selectSamePrimaryContactAddress);
    this.contactType = ContactType.PRIMARY;
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
    this.store.dispatch(setSameAddressPrimaryContact({ sameAddress: event }));
  }
}
