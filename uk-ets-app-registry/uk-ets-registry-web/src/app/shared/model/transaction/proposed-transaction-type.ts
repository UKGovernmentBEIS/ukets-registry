import { TransactionType } from './transaction-type.enum';

export interface ProposedTransactionType {
  type: TransactionType;
  description: string;
  hint?: string;
  category: string;
  supportsNotification: boolean;
  skipAccountStep: boolean;
  isReversal?: boolean;
  order?: number;
}
