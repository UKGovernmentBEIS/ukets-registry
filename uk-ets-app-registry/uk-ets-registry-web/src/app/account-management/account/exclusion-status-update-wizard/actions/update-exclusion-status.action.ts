import { NavigationExtras, Params } from '@angular/router';
import { createAction, props } from '@ngrx/store';
import { VerifiedEmissions } from '@registry-web/account-shared/model';
import { OperatorEmissionsExclusionStatus } from '../model';

export const navigateTo = createAction(
  '[Update Exclusion Status Wizard] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const clearUpdateExclusionStatus = createAction(
  '[Update Exclusion Status Wizard] Clear update exclusion status'
);

export const cancelClicked = createAction(
  '[Update Exclusion Status Wizard] Cancel clicked',
  props<{ route: string }>()
);

export const cancelUpdateExclusionStatus = createAction(
  '[Update Exclusion Status Wizard] Cancel update'
);

export const submitUpdate = createAction(
  '[Update Exclusion Status Wizard] Submit update',
  props<{
    accountIdentifier: string;
    exclusionStatus: OperatorEmissionsExclusionStatus;
  }>()
);

export const submitUpdateSuccess = createAction(
  '[Update Exclusion Status Wizard] Submit update success'
);

export const fetchCurrentAccountEmissionDetailsInfo = createAction(
  '[Update Exclusion Status Wizard] Fetch current account emission details info',
  props<{ compliantEntityIdentifier: number }>()
);

export const setCurrentAccountEmissionDetailsSuccess = createAction(
  '[Update Exclusion Status Wizard] Fetch current account emission details info success',
  props<{ entries: VerifiedEmissions[] }>()
);

export const setExclusionYear = createAction(
  '[Update Exclusion Status Wizard] Set exclusion year',
  props<{ year: number }>()
);

export const setExclusionStatus = createAction(
  '[Update Exclusion Status Wizard] Set exclusion status for specific year',
  props<{ excluded: boolean }>()
);

export const setExclusionReason = createAction(
  '[Update Exclusion Status Wizard] Set exclusion reason for specific year',
  props<{ reason: string }>()
);
