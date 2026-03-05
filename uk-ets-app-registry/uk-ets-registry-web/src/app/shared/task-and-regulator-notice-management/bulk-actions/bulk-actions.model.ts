import { ErrorDetail } from '@registry-web/shared/error-summary';

export interface BulkActionsConfig {
  itemTypeLabel: 'task' | 'notice';
  listPath: string;
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
