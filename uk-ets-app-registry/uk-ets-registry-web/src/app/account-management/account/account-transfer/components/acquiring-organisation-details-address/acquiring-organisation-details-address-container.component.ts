import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  AccountHolder,
  AccountHolderType,
  Organisation,
} from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { Observable } from 'rxjs';
import { AccountTransferPathsModel } from '@account-transfer/model';
import {
  cancelClicked,
  setAcquiringAccountHolderAddress,
} from '@account-transfer/store/actions/account-transfer.actions';
import { selectAcquiringAccountHolder } from '@account-transfer/store/reducers';

@Component({
  selector: 'app-acquiring-organisation-details-address-container',
  template: `
    <app-account-holder-organisation-address
      [isAHUpdateWizard]="false"
      [caption]="'Request account transfer'"
      [header]="'Add the organisation address'"
      [countries]="countries$ | async"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-organisation-address>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AcquiringOrganisationDetailsAddressContainerComponent
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

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountTransferPathsModel.BASE_PATH}/${
          AccountTransferPathsModel.UPDATE_ACCOUNT_HOLDER
        }`,
      })
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.accountHolder$ = this.store.select(selectAcquiringAccountHolder);
    this.countryCodes$ = this.store.select(selectCountryCodes);
  }

  onContinue(acquiringOrganisation: Organisation): void {
    this.store.dispatch(
      setAcquiringAccountHolderAddress({
        acquiringOrganisationAddress: {
          address: acquiringOrganisation.address,
        },
      })
    );
  }

  onError(details: ErrorDetail[]): void {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel(): void {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
