import { RequestType } from '@task-management/model/request-types.enum';
import {
  AccountInfo,
  AcquiringAccountInfo,
  AllowanceTransactionBlockSummary,
  EnvironmentalActivity,
  ProposedTransactionType,
  TransactionBlockSummary,
  TransactionConnectionSummary,
  TransactionType,
} from '@shared/model/transaction';
import { TransactionTaskDetailsBase } from './task-details.model';
import { ItlNotification } from '@shared/model/transaction/itl-notification';

export interface KpIssuanceTaskDetailsDto extends TransactionTaskDetailsBase {
  taskType: RequestType.TRANSACTION_REQUEST;
  transactionIdentifier: string;
  trType: TransactionType.IssueOfAAUsAndRMUs;
  commitmentPeriod: string;
  acquiringAccount: string;
  unitType: string;
  environmentalActivity: EnvironmentalActivity;
  initialQuantity: number;
  consumedQuantity: number;
  pendingQuantity: number;
  remainingQuantity: number;
  quantity: number;
}

export interface IssueAllowancesTaskDetailsDTO
  extends TransactionTaskDetailsBase {
  taskType: RequestType.TRANSACTION_REQUEST;
  transactionIdentifier: string;
  trType: TransactionType.IssueAllowances;
  acquiringAccount: AcquiringAccountInfo;
  blocks: AllowanceTransactionBlockSummary[];
}
// generic transactionImplementation
export interface TransactionTaskDetailsDto extends TransactionTaskDetailsBase {
  taskType: RequestType.TRANSACTION_REQUEST;
  transactionIdentifiers: string[];
  transactionType: ProposedTransactionType;
  transferringAccount: AccountInfo;
  acquiringAccount: AcquiringAccountInfo;
  transactionBlocks: TransactionBlockSummary[];
  itlNotification?: ItlNotification;
  allocationDetails?: string;
  transactionConnectionSummary?: TransactionConnectionSummary;
}

export interface ReturnExcessAllocationTransactionTaskDetailsDTO
  extends TransactionTaskDetailsDto {
  natAcquiringAccount: AcquiringAccountInfo;
  nerAcquiringAccount: AcquiringAccountInfo;
  natTransactionBlocks: TransactionBlockSummary[];
  nerTransactionBlocks: TransactionBlockSummary[];
  natQuantity: number;
  nerQuantity: number;
  natTransactionIdentifier: string;
  nerTransactionIdentifier: string;
}
