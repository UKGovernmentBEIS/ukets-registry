import { CommonTransactionSummary } from '@shared/model/transaction';

export interface TransactionBlock extends CommonTransactionSummary {
  startBlock: number;
  endBlock: number;
  originatingCountryCode: string;
  environmentalActivity: string;
  expiryDate: string;
  quantity: string;
  projectNumber: string;
}
