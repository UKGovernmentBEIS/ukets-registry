import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { accountOpeningFeatureKey } from '../account-opening.reducer';
import {
  AccessRightLabelHintMap,
  ARAccessRights,
} from '../../shared/model/account/authorised-representative';
import { AccountOpeningState } from '../account-opening.model';
import { selectConfigurationRegistry } from '@shared/shared.selector';
import { getConfigurationValue } from '@shared/shared.util';
import { AccountType } from '@shared/model/account';

const selectAccountOpening = createFeatureSelector<State, AccountOpeningState>(
  accountOpeningFeatureKey
);

export const selectCurrentAuthorisedRepresentative = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.currentAuthorisedRepresentative
);

export const selectCurrentAuthorisedRepresentativeAccessRightText =
  createSelector(selectCurrentAuthorisedRepresentative, (currentAR) => {
    {
      if (currentAR) {
        return AccessRightLabelHintMap.get(currentAR.right);
      } else {
        return {
          text: '',
          hint: '',
        };
      }
    }
  });

export const selectFetchedAuthorisedRepresentatives = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.fetchedAuthorisedRepresentatives
);

export const selectAuthorisedRepresentatives = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.authorisedRepresentatives
);

export const selectAuthorisedRepresentativesOptional = createSelector(
  selectAccountOpening,
  (accountOpeningState) =>
    accountOpeningState.accountType === AccountType.OPERATOR_HOLDING_ACCOUNT ||
    accountOpeningState.accountType ===
      AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
);

export const selectAuthorisedRepresentativeViewOrCheck = createSelector(
  selectAccountOpening,
  (accountOpeningState) =>
    accountOpeningState.authorisedRepresentativeViewOrCheck
);

export const selectIfLessThanFiveARsWithOtherThanReadOnly = createSelector(
  selectAccountOpening,
  (accountOpeningState) => {
    return (
      accountOpeningState.authorisedRepresentatives.filter(
        (ar) => ar.right !== ARAccessRights.READ_ONLY
      ).length < 5
    );
  }
);

export const selectAuthorisedRepresentativesCompleted = createSelector(
  selectConfigurationRegistry,
  selectAccountOpening,
  (configuration, accountOpeningState) => {
    const minARs: number = getConfigurationValue(
      'business.property.account.min.number.of.authorised.representatives',
      configuration
    );

    return accountOpeningState.authorisedRepresentatives.length >= minARs;
  }
);
