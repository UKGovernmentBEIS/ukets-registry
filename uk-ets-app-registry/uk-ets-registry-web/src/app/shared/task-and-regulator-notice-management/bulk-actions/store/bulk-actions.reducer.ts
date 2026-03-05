import { createFeature, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  BulkActionsConfig,
  BulkActionSuccess,
} from '@shared/task-and-regulator-notice-management/bulk-actions/bulk-actions.model';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions/store/bulk-actions.actions';

const featureKey = 'bulkActions';

interface BulkActionsState {
  itemTypeLabel: BulkActionsConfig['itemTypeLabel'];
  listPath: BulkActionsConfig['listPath'];
  bulkActionSuccess: BulkActionSuccess;
}

const initialState: BulkActionsState = {
  itemTypeLabel: 'task',
  listPath: null,
  bulkActionSuccess: undefined,
};

const reducer = createReducer(
  initialState,

  mutableOn(BulkActions.SET_CONFIG_VALUES, (state, { payload }) => {
    state.itemTypeLabel = payload.itemTypeLabel;
    state.listPath = payload.listPath;
  }),

  mutableOn(BulkActions.SHOW_SUCCESS_MESSAGE, (state, { payload }) => {
    state.bulkActionSuccess = payload;
  }),

  mutableOn(BulkActions.CLEAR_SUCCESS, (state) => {
    state.bulkActionSuccess = undefined;
  })
);

export const bulkActionsFeature = createFeature({
  name: featureKey,
  reducer,
});
