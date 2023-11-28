export enum RegistryDbRegistryLevelTypeEnum {
  ISSUANCE_KYOTO_LEVEL = 'ISSUANCE_KYOTO_LEVEL',
  AAU_TRANSFER = 'AAU_TRANSFER',
}

export function getRegistryLevelType(value: string) {
  switch (value) {
    case 'Kyoto Issuance':
      return RegistryDbRegistryLevelTypeEnum.ISSUANCE_KYOTO_LEVEL;
    case 'AAU':
      return RegistryDbRegistryLevelTypeEnum.AAU_TRANSFER;
    default:
      return null;
  }
}
