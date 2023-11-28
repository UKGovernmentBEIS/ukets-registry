import { createAction, props } from '@ngrx/store';
import {
  ComplianceOverviewResult,
  ComplianceStatusHistoryResult,
  VerifiedEmissionsResult,
} from '@account-shared/model';
import { DomainEvent } from '@registry-web/shared/model/event';

export const fetchComplianceOverview = createAction(
  '[Emissions and Surrenders] Fetch Compliance Overview',
  props<{ accountIdentifier: number }>()
);

export const fetchComplianceOverviewSuccess = createAction(
  '[Emissions and Surrenders] Fetch Compliance Overview success',
  props<{ result: ComplianceOverviewResult }>()
);

export const fetchVerifiedEmissions = createAction(
  '[Emissions and Surrenders] Fetch Verified Emissions',
  props<{ compliantEntityId: number }>()
);

export const fetchVerifiedEmissionsSuccess = createAction(
  '[Emissions and Surrenders] Fetch Verified Emissions success',
  props<{ results: VerifiedEmissionsResult }>()
);

export const fetchComplianceStatusHistory = createAction(
  '[Emissions and Surrenders] Fetch Compliance Status History',
  props<{ compliantEntityId: number }>()
);

export const fetchComplianceStatusHistorySuccess = createAction(
  '[Emissions and Surrenders] Fetch Compliance Status History success',
  props<{ results: ComplianceStatusHistoryResult }>()
);

export const fetchComplianceHistoryAndComments = createAction(
  '[Emissions and Surrenders] Fetch Compliance History and Comments.',
  props<{ compliantEntityId: number }>()
);

export const fetchComplianceHistoryAndCommentsSuccess = createAction(
  '[Emissions and Surrenders] Fetch Compliance History and Comments success',
  props<{ results: DomainEvent[] }>()
);

export const resetEmissionsAndSurrendersStatusState = createAction(
  '[Emissions and Surrenders] Reset the emissions and surrenders state'
);
