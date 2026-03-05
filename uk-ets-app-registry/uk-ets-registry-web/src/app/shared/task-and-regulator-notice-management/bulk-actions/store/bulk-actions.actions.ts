import { createAction } from '@ngrx/store';
import {
  BulkActionPayload,
  BulkAssignPayload,
  BulkActionSuccess,
  BulkActionsConfig,
} from '@shared/task-and-regulator-notice-management/bulk-actions/bulk-actions.model';

export const BulkActions = {
  SET_CONFIG_VALUES: createAction(
    '[Bulk Actions] Set Bulk Actions config values',
    (payload: BulkActionsConfig) => ({ payload })
  ),
  BULK_CLAIM: createAction(
    '[Bulk Actions] Bulk Claim',
    (payload: BulkActionPayload) => ({ payload })
  ),
  BULK_CLAIM_SUCCESS: createAction(
    '[Bulk Actions] Bulk claim success',
    (payload: BulkActionSuccess) => ({ payload })
  ),
  BULK_ASSIGN: createAction(
    '[Bulk Actions] Bulk assign',
    (payload: BulkAssignPayload) => ({ payload })
  ),
  BULK_ASSIGN_SUCCESS: createAction(
    '[Bulk Actions] Bulk assign success',
    (payload: BulkActionSuccess) => ({ payload })
  ),
  SHOW_SUCCESS_MESSAGE: createAction(
    '[Bulk Actions] Show success message',
    (payload: BulkActionSuccess) => ({ payload })
  ),
  RESET_SUCCESS: createAction(
    '[Bulk Actions] Send command to clear the success message'
  ),
  CLEAR_SUCCESS: createAction('[Bulk Actions] Clear the success message'),
};
