import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountHolderContact } from '@shared/model/account';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { canGoBack, errors } from '@shared/shared.action';
import {
  selectCountryCodes,
  selectAllCountries,
} from '@shared/shared.selector';
import { Observable } from 'rxjs';
import { AccountTransferPathsModel } from '@account-transfer/model';
import {
  cancelClicked,
  setAcquiringAccountHolderPrimaryContactWorkDetails,
  setPrimaryContactWorkAddressSameAsAccountHolderAddress,
} from '@account-transfer/store/actions/account-transfer.actions';
import {
  selectAcquiringAccountHolderAddress,
  selectAcquiringAccountHolderPrimaryContact,
  selectPrimaryContactAddressSameAsAccountHolder,
} from '@account-transfer/store/reducers';

@Component({
  selector: 'app-acquiring-primary-contact-work-details-container',
  template: `
    <app-account-holder-contact-work-details
      [contactType]="contactType"
      [isAHUpdateWizard]="false"
      [accountHolderContact]="accountHolderContact$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (errorDetails)="onError($event)"
      [accountHolderAddress]="accountHolderAddress$ | async"
      [sameAddress]="
        primaryContactWorkAddressSameAsAccountHolderAddress$ | async
      "
      (output)="onContinue($event)"
      (copyAccountHolderAddressToWorkAddress)="onAddressCheckChange($event)"
    ></app-account-holder-contact-work-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AcquiringPrimaryContactWorkDetailsContainerComponent
  implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact>;
  countries$: Observable<IUkOfficialCountry[]>;
  accountHolderAddress$: Observable<any>;
  primaryContactWorkAddressSameAsAccountHolderAddress$: Observable<boolean>;
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  >;
  contactType: string;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit(): void {
    this.countryCodes$ = this.store.select(selectCountryCodes);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountTransferPathsModel.BASE_PATH}/${
          AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT
        }`,
      })
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.accountHolderContact$ = this.store.select(
      selectAcquiringAccountHolderPrimaryContact
    );
    this.accountHolderAddress$ = this.store.select(
      selectAcquiringAccountHolderAddress
    );
    this.primaryContactWorkAddressSameAsAccountHolderAddress$ = this.store.select(
      selectPrimaryContactAddressSameAsAccountHolder
    );
    this.contactType = ContactType.PRIMARY;
  }

  onContinue(value: AccountHolderContact): void {
    this.store.dispatch(
      setAcquiringAccountHolderPrimaryContactWorkDetails({
        acquiringAccountHolderContactWorkDetails: {
          positionInCompany: value.positionInCompany,
          address: value.address,
          phoneNumber: value.phoneNumber,
          emailAddress: value.emailAddress,
        },
      })
    );
  }

  onError(value: ErrorDetail[]): void {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }

  onCancel(): void {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onAddressCheckChange(event: boolean): void {
    this.store.dispatch(
      setPrimaryContactWorkAddressSameAsAccountHolderAddress({
        primaryContactWorkAddressSameAsAccountHolderAddress: event,
      })
    );
  }
}
