import {
  ReturnExcessAllocationTransactionSummary,
  TransactionSummary,
} from '@shared/model/transaction';

interface SigningInfo {
  otpCode: string;
}

export interface SigningRequestInfo extends SigningInfo {
  data: string;
}

export interface TransactionSigningInfo extends SigningInfo {
  transactionSummary: TransactionSummary;
}

export interface ReturnExcessAllocationTransactionSigningInfo
  extends SigningInfo {
  returnExcessAllocationTransactionSummary: ReturnExcessAllocationTransactionSummary;
}

// to be sent to the registry
export interface SignatureInfo {
  signature: string;
  data: string;
  signedBy?: string;
  signedIn?: Date;
}

export enum SignedDataType {
  ETS_WIZARD,
  MAIN_TRANSACTION_WIZARD,
  KP_WIZARD,
  RETURN_EXCESS_ALLOCATION_WIZARD,
}
