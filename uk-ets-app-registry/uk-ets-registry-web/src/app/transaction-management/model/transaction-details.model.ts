import { TransactionResponse } from './transaction-response.model';
import { TransactionBlock } from './transaction-block.model';
import { ItlNotification } from '@shared/model/transaction/itl-notification';
import { TransactionConnectionSummary } from '@shared/model/transaction';
import { AccountStatus } from '@shared/model/account';

export interface TransactionDetails {
  identifier: string;
  taskIdentifier: string;
  type: string; // TODO change to TransactionType
  status: string;
  quantity: string;
  acquiringAccountName: string;
  externalAcquiringAccount: boolean;
  acquiringAccountIdentifier: number;
  acquiringAccountType: string;
  acquiringAccountRegistryCode: string;
  acquiringAccountFullIdentifier: string;
  hasAccessToAcquiringAccount: boolean;
  transferringAccountName: string;
  externalTransferringAccount: boolean;
  transferringAccountIdentifier: number;
  transferringAccountType: string;
  transferringRegistryCode: string;
  transferringAccountFullIdentifier: string;
  hasAccessToTransferringAccount: boolean;
  transferringAccountStatus: AccountStatus;
  acquiringAccountStatus: AccountStatus;
  started: string;
  lastUpdated: Date;
  blocks: Array<TransactionBlock>;
  responses: Array<TransactionResponse>;
  unitType: string;
  attributes?: string;
  itlNotification?: ItlNotification;
  canBeReversed?: boolean;
  transactionConnectionSummary?: TransactionConnectionSummary;
  reference?: string;
  executionDateTime: Date;
}
