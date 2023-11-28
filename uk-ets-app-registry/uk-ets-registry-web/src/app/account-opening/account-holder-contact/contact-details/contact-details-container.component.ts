import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { nextPage, setSameAddress } from '../account-holder-contact.actions';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { MainWizardRoutes } from '../../main-wizard.routes';
import { AccountHolderContactWizardRoutes } from '../account-holder-contact-wizard-properties';
import {
  selectAccountHolderAddress,
  selectAccountHolderContact,
  selectSameHolderAddress,
} from '../account-holder-contact.selector';

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
export class ContactDetailsContainerComponent implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact> = this.store.select(
    selectAccountHolderContact
  );
  countries$: Observable<IUkOfficialCountry[]> = this.store.select(
    selectAllCountries
  );
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  > = this.store.select(selectCountryCodes);

  sameAddress$: Observable<boolean>;
  accountHolderAddress$: Observable<any>;

  contactType: string;
  previousRoute: string;
  nextRoute: string;
  overviewRoute: string;
  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.contactType = this.route.snapshot.paramMap.get('contactType');
    this.previousRoute = `${AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_PERSONAL_DETAILS}/${this.contactType}`;
    this.nextRoute = `${AccountHolderContactWizardRoutes.OVERVIEW}/${this.contactType}`;
    this.overviewRoute = `${AccountHolderContactWizardRoutes.OVERVIEW}/${this.contactType}`;
    this.sameAddress$ = this.store.select(selectSameHolderAddress, {
      contactType: this.contactType,
    });
    this.accountHolderAddress$ = this.store.select(selectAccountHolderAddress);

    this.store.dispatch(
      canGoBack({
        goBackRoute: this.previousRoute,
        extras: { skipLocationChange: true },
      })
    );
    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      nextPage({
        accountHolderContact: value,
        contactType: this.contactType,
      })
    );
    this._router.navigate([this.nextRoute], { skipLocationChange: true });
  }

  onError(value: ErrorDetail[]) {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }

  onAddressCheckChange(event) {
    this.store.dispatch(
      setSameAddress({ sameAddress: event, contactType: this.contactType })
    );
  }
}
