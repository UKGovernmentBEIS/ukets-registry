import { createAction, props } from '@ngrx/store';
import {
  BusinessCheckResult,
  ProposedTransactionType,
  ReturnExcessAllocationTransactionSummary,
  TransactionSummary,
  TransactionType,
  TransactionTypesResult,
} from '@shared/model/transaction';
import { NavigationExtras } from '@angular/router';
import { ItlNotification } from '@shared/model/transaction/itl-notification';

export const fetchLoadAndShowAllowedTransactionTypes = createAction(
  '[Transaction Proposal] Fetch, load and show allowed transaction types',
  props<{ accountId: string }>()
);

export const fetchAllowedTransactionTypesSuccess = createAction(
  '[Transaction Proposal] Successful fetch of transaction types',
  props<{ transactionTypes: TransactionTypesResult }>()
);

export const loadAllowedTransactionTypes = createAction(
  '[Transaction Proposal] Load allowed transaction types',
  props<{ transactionTypes: TransactionTypesResult }>()
);

export const setSelectedTransactionType = createAction(
  '[Transaction Proposal] Set selected transaction type',
  props<{
    proposedTransactionType: ProposedTransactionType;
    itlNotificationId: number;
    clearNextStepsInWizard: boolean;
  }>()
);

export const validateITLNotificationId = createAction(
  '[Transaction Proposal] Validate ITL Notification ID',
  props<{
    itlNotificationId: number;
    transactionType: TransactionType;
  }>()
);

export const validateITLNotificationSuccess = createAction(
  '[Transaction Proposal] Validate ITL Notification ID success',
  props<{ itlNotification: ItlNotification }>()
);

export const navigateToSelectTransactionType = createAction(
  '[Transaction Proposal] Navigate to Select Transaction Type Step'
);

export const navigateToCheckTransactionDetails = createAction(
  '[Transaction Proposal] Navigate to Check Transaction Details Step'
);

export const navigateToCheckExcessAllocationTransactionDetails = createAction(
  '[Transaction Proposal] Navigate to Check Excess Allocation Transaction Details Step'
);

export const navigateToSelectUnitTypesAndQuantity = createAction(
  '[Transaction Proposal] Navigate to Select Unit Type And Quantity Step'
);

export const navigateToSpecifyAcquiringAccount = createAction(
  '[Transaction Proposal] Navigate to Specify Acquiring Account Step'
);

export const navigateToTransactionReferencePage = createAction(
  '[Transaction Proposal] Navigate to Transaction Reference Step'
);

export const setTransactionReference = createAction(
  '[Transaction Proposal] Set Transaction Reference ',
  props<{ reference: string }>()
);

export const goBackButtonInSetTransactionReferencePage = createAction(
  '[Transaction Proposal] Go Back Button in Set transaction reference page'
);

export const goBackButtonInCheckDetailsPage = createAction(
  '[Transaction Proposal] Go Back Button in Check details page'
);

export const goBackButtonInSpecifyUnitTypesAndQuantity = createAction(
  '[Transaction Proposal] Go Back Button in Specify units and quantity page'
);

export const navigateTo = createAction(
  '[Transaction Proposal] Navigate to',
  props<{ route: string; extras?: NavigationExtras }>()
);

export const navigateOutsideTransactionProposal = createAction(
  '[Transaction Proposal] Navigate outside transaction proposal',
  props<{ route: string; extras?: NavigationExtras }>()
);

export const cancelTransactionProposal = createAction(
  '[Transaction Proposal] Cancel transaction proposal'
);

export const clearTransactionProposal = createAction(
  '[Transaction Proposal] Clear transaction proposal'
);

export const validateProposal = createAction(
  '[Transaction Proposal API] Validate Transaction Proposal'
);

export const validateReturnExcessAllocationProposal = createAction(
  '[Transaction Proposal API] Validate Transaction Proposal NAT and NER'
);

export const validateProposalSuccess = createAction(
  '[Transaction Proposal API] Validate Transaction Proposal success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const validateReturnExcessAllocationProposalSuccess = createAction(
  '[Transaction Proposal API] Validate Transaction Proposal success NAT and NER',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const enrichProposalForSigning = createAction(
  '[Transaction Proposal API] Enrich Transaction Proposal to be ready for signing'
);

export const enrichReturnExcessAllocationProposalForSigning = createAction(
  '[Transaction Proposal API] Enrich Return Excess Allocation Transaction Proposal to be ready for signing'
);

export const enrichProposalForSigningSuccess = createAction(
  '[Transaction Proposal API] Enrich Transaction Proposal to be ready for signing success',
  props<{ transactionSummary: TransactionSummary }>()
);

export const enrichReturnExcessAllocationProposalForSigningSuccess =
  createAction(
    '[Transaction Proposal API] Enrich Return Excess Allocation Transaction Proposal to be ready for signing success',
    props<{ transactionSummary: ReturnExcessAllocationTransactionSummary }>()
  );

export const submitProposal = createAction(
  '[Transaction Proposal API] Submit Transaction Proposal'
);

export const submitReturnExcessAllocationProposal = createAction(
  '[Transaction Proposal API] Submit Transaction Proposal NAT and NER'
);

export const submitProposalSuccess = createAction(
  '[Transaction Proposal API] Submit Transaction Proposal success',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const submitReturnExcessAllocationProposalSuccess = createAction(
  '[Transaction Proposal API] Submit Transaction Proposal success NAT and NER',
  props<{ businessCheckResult: BusinessCheckResult }>()
);

export const setCommentAndOtpCode = createAction(
  '[Transaction Proposal] Set comment and otp Code',
  props<{ comment: string; otpCode: string }>()
);

export const cancelClicked = createAction(
  '[Transaction Proposal] Cancel clicked',
  props<{ route: string }>()
);
