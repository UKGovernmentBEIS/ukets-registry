import { UnitType } from '@shared/model/transaction';
import { EnvironmentalActivity } from '@shared/model/transaction';

export enum RegistryLevelType {
  ISSUANCE_KYOTO_LEVEL = 'ISSUANCE_KYOTO_LEVEL'
}

// TODO : This is currently used in Issuange .allign this one with issue of allowances
export interface RegistryLevelInfo {
  id: number;
  unitType: UnitType;
  environmentalActivity: EnvironmentalActivity;
  initialQuantity: number;
  consumedQuantity: number;
  pendingQuantity: number;

  readonly EnvironmentalActivity: EnvironmentalActivity;
}

export interface RegistryLevelInfoResults {
  result: RegistryLevelInfo[];
}
