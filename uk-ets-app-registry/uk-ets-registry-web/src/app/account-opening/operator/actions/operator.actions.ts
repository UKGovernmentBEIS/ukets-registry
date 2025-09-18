import { createAction, props } from '@ngrx/store';
import {
  AircraftOperator,
  Installation,
  InstallationTransfer,
  MaritimeOperator,
  Operator,
} from '@shared/model/account';
import { ApiErrorDetail } from '@shared/api-error';

export const completeWizard = createAction(
  '[Operator Wizard] Complete',
  props<{
    complete: boolean;
  }>()
);
export const deleteOperator = createAction('[Operator Wizard] Delete operator');

export const setOperator = createAction(
  '[Operator Wizard] — Continue',
  props<{ operator: Operator }>()
);

export const fetchExistsMonitoringPlan = createAction(
  '[Operator Wizard] - Verify If Monitoring Plan Exists',
  props<{
    operator: AircraftOperator | MaritimeOperator;
  }>()
);

export const fetchExistsMonitoringPlanSuccess = createAction(
  '[Operator Wizard] - Verify If Monitoring Plan Exists Success',
  props<{
    operator: AircraftOperator | MaritimeOperator;
  }>()
);

export const fetchExistsInstallationPermiId = createAction(
  '[Operator Wizard] - Verify If  Installation Permit Exists',
  props<{
    installation: Installation;
  }>()
);

export const fetchExistsInstallationPermitIdSuccess = createAction(
  '[Operator Wizard] - Verify If  Installation Permit Exists Success',
  props<{
    installation: Installation;
  }>()
);

export const navigateToMainWizard = createAction(
  '[Operator Wizard] - Navigate to the Main wizard',
  props<{
    installation: Installation;
  }>()
);

export const validateInstallationTransfer = createAction(
  '[Operator Wizard] - Validate Installation Transfer',
  props<{
    installationTransfer: InstallationTransfer;
    acquiringAccountHolderIdentifier: number;
  }>()
);

export const validateInstallationTransferSuccess = createAction(
  '[Operator Wizard] - Validate Installation Transfer Success',
  props<{
    installationToBeTransferred: Installation;
  }>()
);

export const initialPermitId = createAction(
  '[Operator Wizard] - Set initial permit id',
  props<{
    permitID: string;
  }>()
);

export const fetchExistsImoAndMonitoringPlan = createAction(
  '[Operator Wizard] - Verify If Monitoring plan and Imo Exists',
  props<{
    operator: MaritimeOperator;
  }>()
);

export const fetchExistsMonitoringPlanAndImoSuccess = createAction(
  '[Operator Wizard] - Verify If Monitoring plan and Imo Exists Success',
  props<{
    operator: MaritimeOperator;
  }>()
);

export const fetchExistsMonitoringPlanAndImoFailure = createAction(
  '[Operator Wizard] - Verify If Monitoring plan and Imo Exists Failure',
  props<{
    errorSummaries: any;
  }>()
);
