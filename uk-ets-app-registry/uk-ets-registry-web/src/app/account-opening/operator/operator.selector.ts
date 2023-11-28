import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { accountOpeningFeatureKey } from '../account-opening.reducer';
import {
  AircraftOperator,
  Installation,
  InstallationTransfer,
  OperatorType,
} from '../../shared/model/account/operator';
import { AccountOpeningState } from '../account-opening.model';
import { selectAccountType } from '@account-opening/account-opening.selector';
import { AccountType } from '@shared/model/account';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { isSeniorOrJuniorAdmin } from '@registry-web/auth/auth.selector';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';

const selectAccountOpening = createFeatureSelector<State, AccountOpeningState>(
  accountOpeningFeatureKey
);

//deprecated  do not use this one. use selectOperator Instead
export const selectInstallation = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.operator as Installation
);

export const selectInstallationPermitDate = createSelector(
  selectAccountOpening,
  (accountOpeningState) => {
    const installation = accountOpeningState.operator as Installation;
    return installation.permit.date;
  }
);

export const selectOperator = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.operator
);

export const selectInstallationToBeTransferred = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.installationToBeTransferred
);
export const selectOperatorType = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState?.operator?.type
);

//deprecated  do not use this one. use selectOperator Instead
export const selectAircraftOperator = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.operator as AircraftOperator
);

export const selectOperatorCompleted = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.operatorCompleted
);

export const selectOperatorWizardLink = createSelector(
  selectOperatorCompleted,
  selectAccountType,
  isSeniorOrJuniorAdmin,
  (operatorCompleted, accountType, isJuniorOrSeniorAdmin) => {
    let operatorWizardLink: string;
    if (operatorCompleted) {
      operatorWizardLink = OperatorWizardRoutes.OVERVIEW;
    } else {
      if (accountType === AccountType.OPERATOR_HOLDING_ACCOUNT) {
        operatorWizardLink = isJuniorOrSeniorAdmin
          ? OperatorWizardRoutes.IS_IT_INSTALLATION_TRANSFER
          : OperatorWizardRoutes.INSTALLATION;
      } else if (
        accountType === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
      ) {
        operatorWizardLink = OperatorWizardRoutes.AIRCRAFT_OPERATOR;
      }
    }
    return operatorWizardLink;
  }
);

export const selectOperatorTextForMainWizard = createSelector(
  selectOperatorCompleted,
  selectAccountType,
  selectOperator,
  selectInstallationToBeTransferred,
  (operatorCompleted, accountType, operator, installationToBeTransferred) => {
    let operatorText = '';
    if (!operatorCompleted) {
      if (accountType === AccountType.OPERATOR_HOLDING_ACCOUNT) {
        operatorText = 'Installation Information';
      } else {
        operatorText = 'Aircraft Operator details';
      }
    } else {
      switch (operator.type) {
        case OperatorType.INSTALLATION: {
          const installation = operator as Installation;
          operatorText = `${installation.permit?.id} ${installation.name}`;
          break;
        }
        case OperatorType.INSTALLATION_TRANSFER: {
          const installationTransfer = operator as InstallationTransfer;
          operatorText = `Installation Transfer (Installation ID:${installationToBeTransferred.identifier}, Old Permit ID:  ${installationToBeTransferred.permit.id} to New Permit ID: ${installationTransfer.permit.id})`;
          break;
        }
        case OperatorType.AIRCRAFT_OPERATOR: {
          const aircraftOperator = operator as AircraftOperator;
          operatorText = `${aircraftOperator.monitoringPlan?.id}`;
          break;
        }
      }
    }
    return operatorText;
  }
);

export const selectInitialPermitId = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.initialPermitId
);

export const selectOperatorInputBackLink = createSelector(
  selectOperator,
  selectOperatorCompleted,
  isSeniorOrJuniorAdmin,
  (operator, operatorCompleted, isJuniorSeniorAdmin) => {
    let backLink = '';
    if (operatorCompleted) {
      backLink = OperatorWizardRoutes.OVERVIEW;
    } else {
      switch (operator?.type) {
        case OperatorType.INSTALLATION:
          if (isJuniorSeniorAdmin) {
            backLink = OperatorWizardRoutes.IS_IT_INSTALLATION_TRANSFER;
          } else {
            backLink = MainWizardRoutes.TASK_LIST;
          }
          break;
        case OperatorType.INSTALLATION_TRANSFER:
          backLink = OperatorWizardRoutes.IS_IT_INSTALLATION_TRANSFER;
          break;
        case OperatorType.AIRCRAFT_OPERATOR:
          backLink = MainWizardRoutes.TASK_LIST;
          break;
      }
    }
    return backLink;
  }
);
