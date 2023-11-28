import { createAction, props } from '@ngrx/store';
import { Account } from '@shared/model/account/account';
import { AccountDetails, AccountHoldingsResult } from '@shared/model/account';
import { DomainEvent } from '@shared/model/event';
import { AccountAllocation } from '@shared/model/account/account-allocation';
import { FileDetails } from '@shared/model/file/file-details.model';
import { ExcessAmountPerAllocationAccount } from '@transaction-proposal/model/transaction-proposal-model';
import { ReturnExcessAllocationType } from '@shared/model/allocation';

export const prepareNavigationToAccount = createAction(
  `[Account Header Guard]
  Prepare the navigation to account details by pre-fetching the account details
  and by setting up the 'can go back' url`,
  props<{ accountId: string }>()
);

export const fetchAccount = createAction(
  '[Account Details Effect] Fetch account',
  props<{ accountId: string }>()
);

export const fetchAccountHistory = createAction(
  '[Account History] Fetch Account history',
  props<{ identifier: string }>()
);

export const fetchAccountOperatorPendingApproval = createAction(
  '[Account History] Fetch Account Operator Update Pending Approval',
  props<{ account: Account }>()
);

export const fetchAccountOperatorPendingApprovalSuccess = createAction(
  '[Account History] Fetch Account Operator Update Pending Approval Success',
  props<{ hasOperatorUpdatePendingApproval: boolean }>()
);

export const fetchAccountHistorySuccess = createAction(
  '[Account History] Fetch account history success',
  props<{ results: DomainEvent[] }>()
);

export const resetAccountHistory = createAction(
  '[Account History] Reset account history'
);

export const fetchAccountHistoryError = createAction(
  '[Account History] Fetch account history error',
  props<{ error?: any }>()
);

export const retrieveAccountHolderFiles = createAction(
  '[Account Holder details] Retrieve account holder files',
  props<{ accountIdentifier: string }>()
);

export const retrieveAccountHolderFilesSuccess = createAction(
  '[Account Holder details] Retrieve account holder files success',
  props<{ accountHolderFiles: FileDetails[] }>()
);

export const resetAccountHolderFiles = createAction(
  '[Account Holder details] Reset account holder files'
);

export const retrieveAccountHolderFilesError = createAction(
  '[Account Holder details] Retrieve account holder files error',
  props<{ error?: any }>()
);

export const fetchAccountHolderFile = createAction(
  '[Account Holder details] Fetch account holder file',
  props<{
    fileId: number;
  }>()
);

export const loadAccountDetails = createAction(
  '[Account Details Effect] Load account',
  props<{ account: Account; accountId: string }>()
);

export const fetchAccountHoldings = createAction(
  '[Account Holdings] Fetch account holdings',
  props<{ accountId: string }>()
);

export const loadAccountHoldings = createAction(
  '[Account Holdings] Load account holdings',
  props<{ accountHoldingsResult: AccountHoldingsResult }>()
);

export const setSideMenu = createAction('[Account Details] Set side menu');

export const fetchAccountAllocation = createAction(
  '[Account Allocation Component On Init] Fetch allocation',
  props<{ accountId: string }>()
);

export const fetchAccountAllocationSuccess = createAction(
  '[Account Effect] Fetch allocation Success',
  props<{ allocation: AccountAllocation }>()
);

export const clearAccountAllocation = createAction(
  '[Account Allocation Component On Destroy] Clear stored allocation'
);

export const navigateToUpdateAccountAllocationWizard = createAction(
  '[Account Allocation Status] Navigate to update wizard',
  props<{ accountId: string }>()
);

export const prepareAccountAllocation = createAction(
  '[Account Allocation Component On Init] Prepare Allocation',
  props<{ accountId: string }>()
);

export const updateAccountDetails = createAction(
  '[Account Details Edit] Proceed to confirm page',
  props<{ accountDetails: AccountDetails }>()
);

export const clearAccountDetailsUpdate = createAction(
  '[Account Details Update Clear] Clear account details update'
);

export const accountDetailsSameBillingAddress = createAction(
  '[Account Details Edit] Toggle the billing address is the same as the account holder address',
  props<{ accountDetailsSameBillingAddress: boolean }>()
);

export const submitAccountDetailsUpdate = createAction(
  '[Account Details Confirm] Submit'
);

export const submitAccountDetailsUpdateSuccess = createAction(
  '[Account Details Confirm] Submit Success',
  props<{ account: Account }>()
);

export const cancelUpdateAccountDetails = createAction(
  '[Account Details Update] Cancel Update Clicked',
  props<{ currentRoute: string }>()
);

export const cancelUpdateAccountDetailsConfirm = createAction(
  '[Account Details Update] Cancel Update Confirm'
);

export const saveExcludeBillingRemarks = createAction(
  '[Account Details Update] Save Exclude Billing Remarks',
  props<{ remarks: string }>()
);

export const cancelExcludeBilling = createAction(
  '[Account Details Update] Cancel Exclude Billing'
);

export const cancelExcludeBillingConfirm = createAction(
  '[Account Details Update] Cancel Exclude Billing Confirm'
);

export const submitExcludeBilling = createAction(
  '[Account Details Update] Submit Exclude Billing',
  props<{ remarks: string }>()
);

export const submitExcludeBillingSuccess = createAction(
  '[Account Details Update] Submit Exclude Billing Success',
  props<{ response: any }>()
);

export const submitIncludeBilling = createAction(
  '[Account Details Update] Submit Include Billing'
);

export const submitIncludeBillingSuccess = createAction(
  '[Account Details Update] Submit Include Billing Success',
  props<{ response: any }>()
);

export const prepareTransactionStateForReturnOfExcess = createAction(
  '[Account Allocation Status] Prepare for return of excess transaction wizard',
  props<{
    routeSnapshotUrl: string;
    allocationYear: number;
    allocationType: ReturnExcessAllocationType;
    excessAmountPerAllocationAccount: ExcessAmountPerAllocationAccount;
  }>()
);

export const prepareForTrustedAccountChangeDescription = createAction(
  '[Account Details Effect] Load account details of trusted account being changed',
  props<{ accountFullIdentifier: string; accountDescription: string }>()
);
