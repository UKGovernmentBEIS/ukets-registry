import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as AccountDetailsActions from './account.actions';
import * as AccountTransactionsActions from './transactions/account-transactions.actions';
import { Account } from '@shared/model/account/account';
import { DomainEvent } from '@shared/model/event';
import {
  AOHA_ITEMS,
  NO_OPERATOR_ITEMS,
  OHAI_ITEMS,
} from './model/account-side-menu.model';
import {
  AccountDetails,
  AccountHoldingsResult,
  AccountType,
} from '@shared/model/account';
import { AccountAllocation } from '@shared/model/account/account-allocation';
import { Pagination } from '@shared/search/paginator';
import { Transaction } from '@shared/model/transaction';
import { FileDetails } from '@shared/model/file/file-details.model';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import {
  ComplianceOverviewResult,
  ComplianceStatusHistoryResult,
  VerifiedEmissionsResult,
} from '@account-shared/model';
import { EmissionsSurrendersActions } from './store/actions';
import * as TrustedAccountActions from './trusted-accounts/trusted-accounts.actions';
import { Note, NoteType } from '@registry-web/shared/model/note';

export const accountFeatureKey = 'account';

export interface AccountState {
  account: Account;
  updateAccountDetails: AccountDetails;
  accountDetailsSameBillingAddress: boolean;
  accountDetailsLoaded: boolean;
  accountId: string;
  accountHistory: Array<DomainEvent>;
  sideMenuItems: string[];
  accountHoldingsResult: AccountHoldingsResult;
  allocation: AccountAllocation;
  verifiedEmissions: VerifiedEmissionsResult;
  complianceStatusHistory: ComplianceStatusHistoryResult;
  complianceOverview: ComplianceOverviewResult;
  complianceHistoryComments: DomainEvent[];
  transactions: Transaction[];
  transactionPagination: Pagination;
  accountHolderFiles: FileDetails[];
  selectedTrustedAccountFullIdentifier: string;
  selectedTrustedAccountDescription: string;
  hasOperatorUpdatePendingApproval: boolean;
  excludeBillingRemarks: string;
}

export const initialState: AccountState = {
  account: {
    identifier: null,
    accountType: null,
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
    addedARs: null,
    removedARs: null,
    kyotoAccountType: false,
  },
  accountDetailsLoaded: false,
  accountDetailsSameBillingAddress: false,
  updateAccountDetails: null,
  accountId: null,
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
  selectedTrustedAccountDescription: null,
  hasOperatorUpdatePendingApproval: null,
  excludeBillingRemarks: null,
};

const accountReducer = createReducer(
  initialState,
  mutableOn(AccountDetailsActions.fetchAccount, (state) => {
    state.accountDetailsLoaded = false;
  }),
  mutableOn(
    AccountDetailsActions.loadAccountDetails,
    (state, { account, accountId }) => {
      const accountData = {
        ...account,
        trustedAccountList: { ...initialState.account.trustedAccountList },
      };
      state.account = accountData;
      state.accountId = accountId;
      state.accountDetailsLoaded = true;
      state.accountDetailsSameBillingAddress =
        account.accountDetailsSameBillingAddress;
    }
  ),
  mutableOn(
    AccountDetailsActions.updateAccountDetails,
    (state, { accountDetails }) => {
      state.updateAccountDetails = {
        ...state.account.accountDetails,
        ...accountDetails,
      };
    }
  ),
  mutableOn(AccountDetailsActions.clearAccountDetailsUpdate, (state) => {
    state.updateAccountDetails = null;
  }),
  mutableOn(
    AccountDetailsActions.fetchAccountOperatorPendingApprovalSuccess,
    (state, { hasOperatorUpdatePendingApproval }) => {
      state.hasOperatorUpdatePendingApproval = hasOperatorUpdatePendingApproval;
    }
  ),
  mutableOn(
    AccountDetailsActions.submitAccountDetailsUpdateSuccess,
    (state, { account }) => {
      state.account = account;
      state.updateAccountDetails = null;
      state.accountDetailsSameBillingAddress =
        account.accountDetailsSameBillingAddress;
    }
  ),
  mutableOn(
    AccountDetailsActions.cancelUpdateAccountDetailsConfirm,
    (state) => {
      state.updateAccountDetails = null;
      state.accountDetailsSameBillingAddress = false;
    }
  ),
  mutableOn(
    AccountDetailsActions.accountDetailsSameBillingAddress,
    (state, { accountDetailsSameBillingAddress }) => {
      state.accountDetailsSameBillingAddress = accountDetailsSameBillingAddress;
    }
  ),
  mutableOn(
    AccountDetailsActions.loadAccountHoldings,
    (state, { accountHoldingsResult }) => {
      state.accountHoldingsResult = accountHoldingsResult;
    }
  ),
  mutableOn(AccountDetailsActions.setSideMenu, (state) => {
    if (state.account.accountType === AccountType.OPERATOR_HOLDING_ACCOUNT) {
      state.sideMenuItems = OHAI_ITEMS;
    } else if (
      state.account.accountType ===
      AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
    ) {
      state.sideMenuItems = AOHA_ITEMS;
    } else {
      state.sideMenuItems = NO_OPERATOR_ITEMS;
    }
  }),
  mutableOn(
    AccountDetailsActions.fetchAccountHistory,
    (state, { identifier }) => {
      state.accountDetailsLoaded = true;
    }
  ),
  mutableOn(
    AccountDetailsActions.fetchAccountHistoryError,
    (state, { error }) => {
      state.accountDetailsLoaded = false;
    }
  ),
  mutableOn(
    AccountDetailsActions.fetchAccountHistorySuccess,
    (state, { results }) => {
      state.accountHistory = results;
      state.accountDetailsLoaded = false;
    }
  ),
  mutableOn(AccountDetailsActions.resetAccountHistory, (state) => {
    state.accountHistory = [];
    state.accountDetailsLoaded = false;
  }),
  mutableOn(
    EmissionsSurrendersActions.fetchComplianceOverviewSuccess,
    (state, { result }) => {
      state.complianceOverview = result;
    }
  ),
  mutableOn(
    EmissionsSurrendersActions.fetchVerifiedEmissionsSuccess,
    (state, { results }) => {
      state.verifiedEmissions = results;
    }
  ),
  mutableOn(
    EmissionsSurrendersActions.fetchComplianceStatusHistorySuccess,
    (state, { results }) => {
      state.complianceStatusHistory = results;
    }
  ),
  mutableOn(
    EmissionsSurrendersActions.fetchComplianceHistoryAndCommentsSuccess,
    (state, { results }) => {
      state.complianceHistoryComments = results;
    }
  ),
  mutableOn(
    EmissionsSurrendersActions.resetEmissionsAndSurrendersStatusState,
    (state) => {
      state.complianceHistoryComments = [];
      state.complianceStatusHistory = null;
      state.verifiedEmissions = null;
      state.complianceOverview = null;
    }
  ),
  mutableOn(
    AccountDetailsActions.prepareForTrustedAccountChangeDescription,
    (state, { accountFullIdentifier, accountDescription }) => {
      state.selectedTrustedAccountFullIdentifier = accountFullIdentifier;
      state.selectedTrustedAccountDescription = accountDescription;
    }
  ),
  mutableOn(
    AccountDetailsActions.fetchAccountAllocationSuccess,
    (state, { allocation }) => {
      state.allocation = allocation;
    }
  ),
  mutableOn(
    AccountDetailsActions.retrieveAccountHolderFilesSuccess,
    (state, { accountHolderFiles }) => {
      state.accountHolderFiles = accountHolderFiles;
    }
  ),
  mutableOn(AccountDetailsActions.resetAccountHolderFiles, (state) => {
    state.accountHolderFiles = [];
  }),
  mutableOn(AccountDetailsActions.clearAccountAllocation, (state) => {
    state.allocation = null;
  }),
  mutableOn(
    AccountDetailsActions.saveExcludeBillingRemarks,
    (state, { remarks }) => {
      state.excludeBillingRemarks = remarks;
    }
  ),
  mutableOn(AccountDetailsActions.cancelExcludeBillingConfirm, (state) => {
    state.excludeBillingRemarks = null;
  }),
  mutableOn(AccountDetailsActions.submitExcludeBillingSuccess, (state) => {
    state.excludeBillingRemarks = null;
  }),
  mutableOn(AccountDetailsActions.submitIncludeBillingSuccess, (state) => {
    state.excludeBillingRemarks = null;
  }),
  mutableOn(
    AccountTransactionsActions.loadAccountTransactions,
    (state, { transactions, pageParameters }) => {
      state.transactions = transactions.items;
      state.transactionPagination = {
        currentPage: transactions.items.length ? pageParameters.page + 1 : 1,
        pageSize: pageParameters.pageSize
          ? pageParameters.pageSize
          : transactions.totalResults,
        totalResults: transactions.totalResults,
      };
    }
  ),
  mutableOn(
    TransactionDetailsActions.prepareTransactionProposalStateForReversalSuccess,
    (state, { reversedAccountInfo }) => {
      state.accountId =
        reversedAccountInfo.transferringAccountInfo.identifier.toString();
    }
  ),
  mutableOn(
    TrustedAccountActions.trustedAccountListLoaded,
    (state, { results, pagination, sortParameters, criteria }) => {
      state.account.trustedAccountList.criteria = criteria;
      state.account.trustedAccountList.pagination = pagination;
      state.account.trustedAccountList.results = results;
      state.account.trustedAccountList.pageParameters = {
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize, // The page size that user selected
      };
      state.account.trustedAccountList.sortParameters = sortParameters;
    }
  ),
  mutableOn(TrustedAccountActions.showHideCriteria, (state, { showHide }) => {
    state.account.trustedAccountList.hideCriteria = showHide;
  })
);

export function reducer(state: AccountState | undefined, action: Action) {
  return accountReducer(state, action);
}
