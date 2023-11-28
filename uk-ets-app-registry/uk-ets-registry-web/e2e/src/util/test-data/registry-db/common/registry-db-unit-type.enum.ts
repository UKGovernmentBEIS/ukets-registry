// possibly this one can be completely removed. use UNIT_TYPE_LABELS Instead.
// no need to maintain second list for the tests

export enum RegistryDbUnitTypeEnum {
  AAU = 'AAU',
  RMU = 'RMU',
  ERU_FROM_AAU = 'ERU_FROM_AAU',
  ERU_FROM_RMU = 'ERU_FROM_RMU',
  CER = 'CER',
  TCER = 'TCER',
  LCER = 'LCER',
  NON_KYOTO = 'NON_KYOTO',
  MULTIPLE = 'MULTIPLE',
  ALLOWANCE = 'ALLOWANCE',
}

export function getUnitType(value: string) {
  switch (value) {
    case 'AAU':
      return RegistryDbUnitTypeEnum.AAU;
    case 'RMU':
      return RegistryDbUnitTypeEnum.RMU;
    case 'ERU_FROM_AAU':
      return RegistryDbUnitTypeEnum.ERU_FROM_AAU;
    case 'ERU_FROM_RMU':
      return RegistryDbUnitTypeEnum.ERU_FROM_RMU;
    case 'CER':
      return RegistryDbUnitTypeEnum.CER;
    case 'TCER':
      return RegistryDbUnitTypeEnum.TCER;
    case 'LCER':
      return RegistryDbUnitTypeEnum.LCER;
    case 'NON_KYOTO':
      return RegistryDbUnitTypeEnum.NON_KYOTO;
    case 'MULTIPLE':
      return RegistryDbUnitTypeEnum.MULTIPLE;
    case 'ALLOWANCE':
      return RegistryDbUnitTypeEnum.ALLOWANCE;
    default:
      return null;
  }
}
