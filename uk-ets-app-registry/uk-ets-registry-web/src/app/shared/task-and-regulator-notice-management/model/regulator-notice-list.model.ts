import { PageParameters, Pagination } from '@shared/search/paginator';
import { ErrorDetail } from '@shared/error-summary';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { TaskStatus } from '@shared/task-and-regulator-notice-management/model';

export interface RegulatorNoticeSearchCriteria {
  accountNumber: string;
  accountHolderId: string;
  processType: string;
  taskStatus: TaskStatus;
  operatorId: string;
  permitOrMonitoringPlanIdentifier: string;
  claimantName: string;
}

export interface RegulatorNoticeTask {
  requestId: string;
  accountHolderName: string;
  processType: string;
  permitOrMonitoringPlanIdentifier: string;
  initiatedDate: string;
  claimantName: string;
  taskStatus: TaskStatus;
}

export interface SearchRegulatorNoticesResponse {
  results: RegulatorNoticeTask[];
  pagination: Pagination;
  criteria: RegulatorNoticeSearchCriteria;
  errorMap: Map<any, ErrorDetail>;
}

export interface SearchRegulatorNoticesActionPayload {
  criteria: RegulatorNoticeSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  isReport?: boolean;
  loadPageParametersFromState?: boolean;
}
