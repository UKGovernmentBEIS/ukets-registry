import { createAction, props } from '@ngrx/store';
import { Reconciliation } from '@shared/model/reconciliation-model';

export const fetchLatestKpReconciliation = createAction(
  '[ITL Reconciliation] Fetch Latest KP Reconciliation'
);

export const updateLatestKpReconciliation = createAction(
  '[ITL Reconciliation]] Update Latest KP Reconciliation',
  props<{ reconciliation: Reconciliation }>()
);
