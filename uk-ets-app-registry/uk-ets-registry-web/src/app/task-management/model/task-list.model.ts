import { PageParameters, Pagination } from '@shared/search/paginator';
import { ErrorDetail } from '@shared/error-summary';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { ApiErrorBody, ApiErrorDetail } from '@shared/api-error/api-error';
import { Status } from '@shared/model/status';
import { TaskOutcome } from './task-details.model';

export type TaskStatus = 'OPEN' | 'UNCLAIMED' | 'CLAIMED' | 'COMPLETED';

export interface TaskSearchCriteria {
  accountNumber: string;
  accountHolder: string;
  taskStatus: TaskStatus;
  claimantName: string;
  taskType: string;
  requestId: string;
  claimedOnFrom: string;
  claimedOnTo: string;
  createdOnFrom: string;
  createdOnTo: string;
  completedOnFrom: string;
  completedOnTo: string;
  transactionId: string;
  taskOutcome: string;
  initiatorName: string;
  accountType: string;
  excludeUserTasks: boolean;
  initiatedBy: string;
  claimedBy: string;
  allocationCategory: string;
  allocationYear: string;
  nameOrUserId: string;
  urid?: string;
}

export class Task {
  requestId: string;
  taskType: string;
  initiatorName: string;
  claimantName: string;
  completedByName: string;
  accountNumber: string;
  accountFullIdentifier: string;
  accountType: string;
  kyotoAccountType: string;
  registryAccountType: string;
  accountHolder: string;
  authorisedRepresentative: string;
  authorizedRepresentativeUserId: string;
  transactionId: string;
  createdOn: string;
  taskStatus: TaskStatus;
  requestStatus: TaskOutcome;
  initiatedDate: string;
  claimedDate: string;
  completedDate: string;
  currentUserClaimant: boolean;
  recipientAccountNumber: string;
  accountStatus: string;
  userHasAccess: boolean;
  accountTypeLabel?: string;
}

export interface AddCommentSuccess {
  requestId: string;
}

export class SearchTasksResponse {
  results: Task[];
  pagination: Pagination;
  criteria: TaskSearchCriteria;
  errorMap: Map<any, ErrorDetail>;
}

export interface SearchActionPayload {
  criteria: TaskSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  isReport?: boolean;
  loadPageParametersFromState?: boolean;
}

export enum Mode {
  LOAD,
  CACHE,
}

export class SelectionChange<T> {
  /** Options that were added to the model. */
  added: T[];
  /** Options that were removed from the model. */
  removed: T[];

  constructor(added: T[], removed: T[]) {
    this.added = added;
    this.removed = removed;
  }
}

export interface BulkActionPayload {
  requestIds: string[];
  comment: string;
  potentialErrors: Map<any, ErrorDetail>;
}

export interface BulkAssignPayload extends BulkActionPayload {
  urid: string;
}

export interface BulkActionSuccessResponse {
  updated: number;
}

export interface BulkActionSuccess {
  message: string;
}

export type ActionError = Required<
  Pick<ApiErrorDetail, 'code' | 'urid' | 'message'>
> & { requestId: number };

export interface TaskActionErrorResponse {
  message?: string;
  errors?: ActionError[];
}

export function apiErrorToBusinessError(
  apiErrorBody: ApiErrorBody
): TaskActionErrorResponse {
  const actionErrors: ActionError[] = [];
  apiErrorBody.errorDetails.forEach((errorDetail) => {
    actionErrors.push({
      code: errorDetail.code,
      message: errorDetail.message,
      requestId: Number.parseInt(errorDetail.identifier, 10),
      urid: errorDetail.urid,
    });
  });
  return {
    errors: actionErrors,
  };
}

export const taskStatusMap: Record<TaskStatus, Status> = {
  OPEN: { color: 'grey', label: 'Open' },
  UNCLAIMED: { color: 'grey', label: 'Unclaimed' },
  CLAIMED: { color: 'yellow', label: 'Claimed' },
  COMPLETED: { color: 'green', label: 'Completed' },
};
