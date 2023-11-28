import { createAction, props } from '@ngrx/store';
import { AllowanceReport } from '../../model';
import { DomainEvent } from '@shared/model/event';

export const loadIssuanceAllocationStatus = createAction(
  '[Issuance and Allocation Status] Load Issuance and Allocation Status Table Data'
);

export const loadIssuanceAllocationStatusSuccess = createAction(
  '[Issuance and Allocation Status] Load Issuance and Allocation Status Table Data Success',
  props<{ results: AllowanceReport[] }>()
);

export const loadAllocationTableEventHistory = createAction(
  '[Issuance and Allocation Status] Load Allocation table events history'
);

export const loadAllocationTableEventHistorySuccess = createAction(
  '[Issuance and Allocation Status] Load Allocation table history success',
  props<{ results: DomainEvent[] }>()
);
