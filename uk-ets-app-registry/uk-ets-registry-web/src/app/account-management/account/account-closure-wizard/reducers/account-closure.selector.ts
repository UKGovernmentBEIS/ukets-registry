import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  accountClosureFeatureKey,
  AccountClosureWizardState,
} from '@account-management/account/account-closure-wizard/reducers/account-closure.reducer';
import { empty } from '@shared/shared.util';

const selectAccountClosureState =
  createFeatureSelector<AccountClosureWizardState>(accountClosureFeatureKey);

export const selectClosureComment = createSelector(
  selectAccountClosureState,
  (state) => state.closureComment
);
export const selectClosureDetails = createSelector(
  selectAccountClosureState,
  (state) => state.accountDetails
);

export const selectPendingAllocationTaskExists = createSelector(
  selectAccountClosureState,
  (state) => state.pendingAllocationTaskExists
);

export const selectAllocationClassification = createSelector(
  selectAccountClosureState,
  (state) =>
    !empty(state.allocations)
      ? state.allocations.allocationClassification
      : null
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectAccountClosureState,
  (state) => state.submittedRequestIdentifier
);
