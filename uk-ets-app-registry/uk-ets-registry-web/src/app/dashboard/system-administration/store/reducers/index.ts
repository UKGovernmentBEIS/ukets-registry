import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  SystemAdministrationState,
  systemAdministrationFeatureKey
} from './system-administration.reducer';

export * from './system-administration.reducer';

const selectSystemAdministrationState = createFeatureSelector<
  SystemAdministrationState
>(systemAdministrationFeatureKey);

export const selectResetDatabaseResult = createSelector(
  selectSystemAdministrationState,
  state => state.resetDatabaseResult
);
