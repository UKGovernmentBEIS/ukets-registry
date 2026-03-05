import { PageParameters, Pagination } from '@shared/search/paginator';
import { ErrorDetail } from '@shared/error-summary';
import { SortParameters } from '@shared/search/sort/SortParameters';
import {
  TaskOutcome,
  TaskStatus,
} from './task-and-regulator-notice-management.model';

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
  deadlineFrom: string;
  deadlineTo: string;
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
