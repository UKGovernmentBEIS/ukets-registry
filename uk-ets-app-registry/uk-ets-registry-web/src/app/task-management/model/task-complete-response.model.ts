import { TaskDetails } from '@task-management/model';

// TODO: This will be much simplified if we just return the taskDetailsDTO instead of the response.
export interface TaskCompleteResponseBase {
  requestIdentifier: string;
  taskDetailsDTO: TaskDetails;
}

export interface TransactionProposalCompleteResponse
  extends TaskCompleteResponseBase {
  transactionIdentifier: string;
  executionTime: string;
  executionDate: string;
}

export interface RequestAllocationProposalCompleteResponse
  extends TaskCompleteResponseBase {
  executionTime: string;
  executionDate: string;
}

export type TaskCompleteResponse =
  | TaskCompleteResponseBase
  | TransactionProposalCompleteResponse
  | RequestAllocationProposalCompleteResponse;
