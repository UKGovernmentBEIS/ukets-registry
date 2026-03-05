import { ErrorDetail } from '@shared/error-summary';
import { BulkActionsConfig } from '@shared/task-and-regulator-notice-management/bulk-actions/bulk-actions.model';

export function createBulkClaimErrorMap(
  itemTypeLabel: BulkActionsConfig['itemTypeLabel']
) {
  const errorMap = new Map<any, ErrorDetail>();
  errorMap.set(
    'other',
    new ErrorDetail(null, `Unexpected error on claiming ${itemTypeLabel}s`)
  );
  return errorMap;
}

export function createBulkAssignErrorMap(
  itemTypeLabel: BulkActionsConfig['itemTypeLabel']
) {
  const errorMap = new Map<any, ErrorDetail>();
  errorMap.set(
    'other',
    new ErrorDetail(null, `Unexpected error on assigning ${itemTypeLabel}s`)
  );
  return errorMap;
}
