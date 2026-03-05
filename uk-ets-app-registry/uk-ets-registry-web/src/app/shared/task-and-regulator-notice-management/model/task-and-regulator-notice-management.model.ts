import { ApiErrorBody, ApiErrorDetail } from '@shared/api-error/api-error';
import { Status } from '@shared/model/status';
import { RequestType } from './request-types.enum';

export type TaskStatus = 'OPEN' | 'UNCLAIMED' | 'CLAIMED' | 'COMPLETED';

export enum TaskOutcome {
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  SUBMITTED_NOT_YET_APPROVED = 'SUBMITTED_NOT_YET_APPROVED',
}

export const requestStatusMap: Record<TaskOutcome, Status> = {
  SUBMITTED_NOT_YET_APPROVED: { color: 'green', label: 'Submitted' },
  APPROVED: { color: 'green', label: 'Approved' },
  REJECTED: { color: 'red', label: 'Rejected' },
};

export interface TaskFileDownloadInfo {
  fileId?: number;
  taskType: RequestType;
  taskRequestId: string;
}

export enum ListMode {
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
