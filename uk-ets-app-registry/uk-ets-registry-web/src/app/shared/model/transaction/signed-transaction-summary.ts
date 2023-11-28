import {
  ReturnExcessAllocationTransactionSummary,
  TransactionSummary,
} from '@shared/model/transaction/transaction-summary';
import { SignatureInfo } from '@signing/model';

export interface SignedTransactionSummary extends TransactionSummary {
  signatureInfo: SignatureInfo;
}

export interface SignedReturnExcessAllocationTransactionSummary
  extends ReturnExcessAllocationTransactionSummary {
  signatureInfo: SignatureInfo;
}
