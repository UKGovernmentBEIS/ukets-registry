import { Notice } from '@kp-administration/itl-notices/model';
import { Reconciliation } from '@shared/model/reconciliation-model';

export interface AlertsModel {
  searchResponseResults: Notice[];
  itlReconcileDTO: Reconciliation;
  reconciliationDTO: Reconciliation;
}
