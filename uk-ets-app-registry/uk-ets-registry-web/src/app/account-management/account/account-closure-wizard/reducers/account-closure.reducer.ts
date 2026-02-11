import { createReducer } from '@ngrx/store';
import { AccountAllocation, AccountDetails } from '@shared/model/account';
import { mutableOn } from '@shared/mutable-on';
import {
  clearAccountClosureState,
  fetchAccountAllocationForAccountClosureSuccess,
  fetchAccountPendingAllocationTaskExistsForAccountClosureSuccess,
  setClosureComment,
  setClosureCommentSuccess,
  submitClosureRequestSuccess,
} from '@account-management/account/account-closure-wizard/actions';
import { Draft } from 'immer';

export const accountClosureFeatureKey = 'account-closure-wizard';

export interface AccountClosureWizardState {
  accountDetails: AccountDetails;
  closureComment: string;
  submittedRequestIdentifier: string;
  allocations: AccountAllocation;
  pendingAllocationTaskExists: boolean;
}

export const initialState: AccountClosureWizardState = {
  accountDetails: {
    accountNumber: null,
    publicAccountIdentifier: null,
    address: null,
    accountStatus: null,
    accountType: null,
    accountHolderName: null,
    accountHolderId: null,
    name: null,
    complianceStatus: null,
    openingDate: null,
    closingDate: null,
    closureReason: null,
    billingEmail1: null,
    billingEmail2: null,
    billingContactDetails: null,
    sellingAllowances: null,
  },
  closureComment: null,
  submittedRequestIdentifier: null,
  allocations: {
    standard: null,
    underNewEntrantsReserve: null,
    allocationClassification: null,
    groupedAllocations: null,
    totals: null,
  },
  pendingAllocationTaskExists: false,
};

export const accountClosureWizardReducer = createReducer(
  initialState,
  mutableOn(
    setClosureComment,
    (state: Draft<AccountClosureWizardState>, { closureComment }) => {
      state.closureComment = closureComment;
    }
  ),
  mutableOn(clearAccountClosureState, (state) => {
    resetState(state);
  }),
  mutableOn(
    setClosureCommentSuccess,
    (state, { closureComment, accountDetails }) => {
      state.accountDetails = accountDetails;
      state.closureComment = closureComment;
    }
  ),
  mutableOn(submitClosureRequestSuccess, (state, { requestId }) => {
    state.submittedRequestIdentifier = requestId;
  }),
  mutableOn(
    fetchAccountAllocationForAccountClosureSuccess,
    (state, { allocation }) => {
      state.allocations = allocation;
    }
  ),
  mutableOn(
    fetchAccountPendingAllocationTaskExistsForAccountClosureSuccess,
    (state, { pendingAllocationTaskExists }) => {
      state.pendingAllocationTaskExists = pendingAllocationTaskExists;
    }
  )
);

function resetState(state: AccountClosureWizardState) {
  state.accountDetails = initialState.accountDetails;
  state.closureComment = initialState.closureComment;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
  state.allocations = initialState.allocations;
}
