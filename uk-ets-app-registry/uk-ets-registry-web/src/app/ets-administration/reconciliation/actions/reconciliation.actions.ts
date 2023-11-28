import { createAction, props } from '@ngrx/store';
import { Reconciliation } from '@shared/model/reconciliation-model';

export const startReconciliation = createAction(
  '[Reconciliation administration form] Start Reconciliation'
);

export const fetchLastStartedReconciliation = createAction(
  '[Reconciliation administration form] Get last reconciliation'
);

export const updateLatestReconciliation = createAction(
  '[Reconciliation effect] Set Reconciliation',
  props<{ reconciliation: Reconciliation }>()
);
