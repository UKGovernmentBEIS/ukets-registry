export enum RegistryDbCommitmentPeriodEnum {
  CP0 = 'CP0',
  CP1 = 'CP1',
  CP2 = 'CP2',
  CP3 = 'CP3',
}

export function getCommitmentPeriod(value: string) {
  switch (value) {
    case '0':
      return RegistryDbCommitmentPeriodEnum.CP0;
    case '1':
      return RegistryDbCommitmentPeriodEnum.CP1;
    case '2':
      return RegistryDbCommitmentPeriodEnum.CP2;
    case '3':
      return RegistryDbCommitmentPeriodEnum.CP3;
  }
}
