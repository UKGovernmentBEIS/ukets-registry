import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AccountHolderAddress } from '@shared/model/account';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { ContactType } from '@shared/model/account-holder-contact-type';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import {
  selectAcquiringAccountHolderAddress,
  selectAcquiringAccountHolderContact,
  selectSamePrimaryContactAddress,
} from '@change-account-holder-wizard/store/reducers';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions/';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';

@Component({
  selector: 'app-contact-details-container',
  template: `
    <app-account-holder-contact-work-details
      [contactType]="contactType"
      [accountHolderContact]="accountHolderContact$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (errorDetails)="onError($event)"
      [accountHolderAddress]="accountHolderAddress$ | async"
      [sameAddress]="sameAddress$ | async"
      (output)="onContinue($event)"
      (copyAccountHolderAddressToWorkAddress)="onAddressCheckChange($event)"
    ></app-account-holder-contact-work-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactWorkDetailsContainerComponent implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact> = this.store.select(
    selectAcquiringAccountHolderContact
  );

  countries$: Observable<IUkOfficialCountry[]> =
    this.store.select(selectAllCountries);

  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  > = this.store.select(selectCountryCodes);

  sameAddress$: Observable<boolean>;
  accountHolderAddress$: Observable<AccountHolderAddress>;

  readonly contactType = ContactType.PRIMARY;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.sameAddress$ = this.store.select(selectSamePrimaryContactAddress);

    this.accountHolderAddress$ = this.store.select(
      selectAcquiringAccountHolderAddress
    );

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${ChangeAccountHolderWizardPathsModel.PRIMARY_CONTACT}`,
        extras: { skipLocationChange: true },
      })
    );
    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.setAccountHolderContactAddress({
        contact: value,
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }

  onAddressCheckChange(event) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.setSameAddressPrimaryContact({
        sameAddress: event,
      })
    );
  }
}
