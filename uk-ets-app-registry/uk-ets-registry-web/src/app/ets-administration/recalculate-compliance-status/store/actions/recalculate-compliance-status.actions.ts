import { createAction } from '@ngrx/store';

export const startRecalculatioComplianceStatuAllCompliantEntities =
  createAction(
    '[Recalculate Compliance Status] Start Recalculation for all compliant entities'
  );

export const startRecalculatioComplianceStatuAllCompliantEntitiesSuccess =
  createAction(
    '[Recalculate Compliance Status] Start Recalculation for all compliant entities success'
  );

export const clearRecalculatioComplianceStatuAllCompliantEntities =
  createAction(
    '[Recalculate Compliance Status] Clear Recalculation for all compliant entities.'
  );
