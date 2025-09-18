import { UkDate } from '@shared/model/uk-date';
import {
  InstallationActivityType,
  OperatorType,
  Regulator,
} from '@shared/model/account';

export interface OperatorUpdate {
  type: OperatorType;
  name?: string;
  activityType?: InstallationActivityType;
  permit?: {
    id: string;
    date: UkDate;
  };
  emitterId: string;
  monitoringPlan?: {
    id: string;
  };
  imo?:string,
  regulator?: Regulator;
  changedRegulator?: Regulator;
  firstYear?: string;
  lastYear?: string;
  lastYearChanged?: boolean;
}
