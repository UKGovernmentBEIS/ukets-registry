import { TransactionBlockSummary } from './transaction-block-summary';
import { TransactionType } from './transaction-type.enum';
import { ItlNotification } from '@shared/model/transaction/itl-notification';
import { TransactionStatus } from '@shared/model/transaction/transaction-list';
import { ReturnExcessAllocationType } from '@shared/model/allocation/allocation.model';
import { AcquiringAccountInfo } from './account-info';

export interface TransactionSummary {
  identifier?: string;
  reversedIdentifier?: string;
  transactionConnectionSummary?: TransactionConnectionSummary;
  acquiringAccountIdentifier?: number;
  acquiringAccountFullIdentifier?: string;
  toBeReplacedBlocksAccountFullIdentifier?: string;
  transferringAccountIdentifier?: number;
  attributes?: string;
  itlNotification?: ItlNotification;
  type: TransactionType;
  blocks: TransactionBlockSummary[];
  comment: string;
  allocationYear?: number;
  allocationType?: string;
  reference?: string;
  returnExcessAllocationType?: ReturnExcessAllocationType;
}

export interface TransactionConnectionSummary {
  originalIdentifier: string;
  originalStatus: TransactionStatus;
  reversalIdentifier: string;
  reversalStatus: TransactionStatus;
}

export interface ReturnExcessAllocationTransactionSummary {
  natQuantity: number;
  nerQuantity: number;
  natReturnTransactionIdentifier: string;
  nerReturnTransactionIdentifier: string;
  natAcquiringAccountInfo: AcquiringAccountInfo;
  nerAcquiringAccountInfo: AcquiringAccountInfo;
  transferringAccountIdentifier: number;
  attributes?: string;
  type: TransactionType;
  blocks: TransactionBlockSummary[];
  comment: string;
  allocationYear: number;
  reference?: string;
  returnExcessAllocationType: ReturnExcessAllocationType;
}
