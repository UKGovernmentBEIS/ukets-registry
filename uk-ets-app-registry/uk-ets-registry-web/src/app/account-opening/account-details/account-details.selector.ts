import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { accountOpeningFeatureKey } from '../account-opening.reducer';
import { selectAllCountries } from '../../shared/shared.selector';
import { IUkOfficialCountry } from '../../shared/countries/country.interface';
import { AccountDetails } from '../../shared/model/account/account-details';
import { AccountOpeningState } from '../account-opening.model';

const selectAccountOpening = createFeatureSelector<State, AccountOpeningState>(
  accountOpeningFeatureKey
);

export const selectAccountDetails = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.accountDetails
);

export const selectAccountDetailsCompleted = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.accountDetailsCompleted
);

export const selectAccountDetailsSameBillingAddress = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.accountDetailsSameBillingAddress
);

export const selectAccountDetailsCountry = createSelector(
  selectAccountDetails,
  selectAllCountries,
  (accountDetails: AccountDetails, allCountries: IUkOfficialCountry[]) => {
    if (accountDetails && allCountries) {
      return allCountries.find(
        country => country.key === accountDetails.address.country
      ).item[0].name;
    }
  }
);
