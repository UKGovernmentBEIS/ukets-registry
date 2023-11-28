import {
  AccountState,
  reducer,
} from '@account-management/account/account-details/account.reducer';
import { AccountType } from '@shared/model/account';
import {
  fetchAccount,
  fetchAccountAllocationSuccess,
  loadAccountDetails,
  clearAccountAllocation,
} from '@account-management/account/account-details/account.actions';
import { verifyBeforeAndAfterActionDispatched } from '../../../../testing/helpers/reducer.test.helper';
import { AccountAllocation } from '@shared/model/account/account-allocation';

describe('Account Details reducer', () => {
  const account = {
    identifier: 1023,
    updateAccountDetails: null,
    accountDetailsSameBillingAddress: false,
    accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
    accountHolder: null,
    accountHolderContactInfo: null,
    accountDetails: null,
    operator: null,
    authorisedRepresentatives: null,
    trustedAccountListRules: null,
    complianceStatus: null,
    balance: null,
    unitType: null,
    governmentAccount: null,
    trustedAccountList: {
      pageParameters: {
        page: 0,
        pageSize: 10,
      },
      pagination: null,
      sortParameters: {
        sortField: 'accountFullIdentifier',
        sortDirection: 'ASC',
      },
      results: null,
      criteria: null,
      hideCriteria: false,
    },
    transactionsAllowed: null,
    canBeClosed: null,
    pendingARRequests: null,
    kyotoAccountType: null,
  };

  const fetchedAccount = {
    identifier: 10001,
    accountType: AccountType.PERSON_HOLDING_ACCOUNT,
    updateAccountDetails: null,
    accountHolder: null,
    accountHolderContactInfo: null,
    accountDetails: null,
    operator: null,
    authorisedRepresentatives: null,
    trustedAccountListRules: null,
    complianceStatus: null,
    balance: null,
    unitType: null,
    governmentAccount: null,
    trustedAccountList: {
      pageParameters: {
        page: 0,
        pageSize: 10,
      },
      pagination: null,
      sortParameters: {
        sortField: 'accountFullIdentifier',
        sortDirection: 'ASC',
      },
      results: null,
      criteria: null,
      hideCriteria: false,
    },
    transactionsAllowed: null,
    canBeClosed: null,
    pendingARRequests: null,
    kyotoAccountType: null,
  };

  it('After fetch account details action have been dispatched, the account details loaded flag should be set to false', () => {
    verifyBeforeAndAfterActionDispatched<AccountState>(
      reducer,
      {
        selectedTrustedAccountDescription: null,
        account,
        accountDetailsLoaded: true,
        accountDetailsSameBillingAddress: false,
        updateAccountDetails: null,
        accountId: '10100',
        accountHistory: [],
        sideMenuItems: null,
        accountHoldingsResult: null,
        allocation: null,
        verifiedEmissions: null,
        complianceStatusHistory: null,
        complianceOverview: null,
        complianceHistoryComments: [],
        transactions: [],
        transactionPagination: null,
        accountHolderFiles: [],
        selectedTrustedAccountFullIdentifier: null,
        hasOperatorUpdatePendingApproval: false,
      },
      (state) => {
        expect(state.accountDetailsLoaded).toBeTruthy();
      },
      fetchAccount({ accountId: '213213' }),
      (state) => expect(state.accountDetailsLoaded).toBeFalsy()
    );
  });

  it(`After load account details action have been dispatched, the account details loaded flag should be set to true
  and the new account and account id should be loaded to store.`, () => {
    verifyBeforeAndAfterActionDispatched<AccountState>(
      reducer,
      {
        selectedTrustedAccountDescription: null,
        account,
        accountDetailsLoaded: false,
        accountDetailsSameBillingAddress: false,
        updateAccountDetails: null,
        accountId: account.identifier.toString(),
        accountHistory: [],
        sideMenuItems: null,
        accountHoldingsResult: null,
        allocation: null,
        verifiedEmissions: null,
        complianceStatusHistory: null,
        complianceOverview: null,
        complianceHistoryComments: [],
        transactions: [],
        transactionPagination: null,
        accountHolderFiles: [],
        selectedTrustedAccountFullIdentifier: null,
        hasOperatorUpdatePendingApproval: false,
      },
      (state) => {
        expect(state.accountDetailsLoaded).toBeFalsy();
        expect(state.accountId).toBe(account.identifier.toString());
        expect(state.account).toBe(account);
      },
      loadAccountDetails({
        account: fetchedAccount,
        accountId: fetchedAccount.identifier.toString(),
      }),
      (state) => {
        expect(state.account).toStrictEqual(fetchedAccount);
        expect(state.accountId).toBe(fetchedAccount.identifier.toString());
        expect(state.accountDetailsLoaded).toBeTruthy();
      }
    );
  });

  it(`When load account allocation load action  have been dispatched, then the account allocation should be set`, () => {
    const allocation: AccountAllocation = {
      standard: null,
      underNewEntrantsReserve: null,
    };
    verifyBeforeAndAfterActionDispatched<AccountState>(
      reducer,
      {
        selectedTrustedAccountDescription: null,
        accountHistory: [],
        account,
        updateAccountDetails: null,
        accountDetailsSameBillingAddress: false,
        accountDetailsLoaded: false,
        accountId: account.identifier.toString(),
        sideMenuItems: null,
        accountHoldingsResult: null,
        allocation: null,
        verifiedEmissions: null,
        complianceStatusHistory: null,
        complianceOverview: null,
        complianceHistoryComments: [],
        transactions: [],
        transactionPagination: null,
        accountHolderFiles: [],
        selectedTrustedAccountFullIdentifier: null,
        hasOperatorUpdatePendingApproval: false,
      },
      (state) => {
        expect(state.allocation).toBeFalsy();
      },
      fetchAccountAllocationSuccess({
        allocation,
      }),
      (state) => {
        expect(state.allocation).toBe(allocation);
      }
    );
  });

  it('When clear account allocation action dispatched, then the allocation should be cleared', () => {
    const allocation: AccountAllocation = {
      standard: null,
      underNewEntrantsReserve: null,
    };

    verifyBeforeAndAfterActionDispatched<AccountState>(
      reducer,
      {
        selectedTrustedAccountDescription: null,
        accountHistory: [],
        account,
        updateAccountDetails: null,
        accountDetailsSameBillingAddress: false,
        accountDetailsLoaded: false,
        accountId: account.identifier.toString(),
        sideMenuItems: null,
        accountHoldingsResult: null,
        allocation,
        verifiedEmissions: null,
        complianceStatusHistory: null,
        complianceOverview: null,
        complianceHistoryComments: [],
        transactions: [],
        transactionPagination: null,
        accountHolderFiles: [],
        selectedTrustedAccountFullIdentifier: null,
        hasOperatorUpdatePendingApproval: false,
      },
      (state) => {
        expect(state.allocation).toBeTruthy();
      },
      clearAccountAllocation(),
      (state) => {
        expect(state.allocation).toBeFalsy();
      }
    );
  });
});
