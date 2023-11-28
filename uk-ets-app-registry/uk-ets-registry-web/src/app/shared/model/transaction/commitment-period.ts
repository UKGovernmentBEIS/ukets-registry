export enum CommitmentPeriod {
  CP0 = '0',
  CP1 = '1',
  CP2 = '2',
  CP3 = '3',
}

export const COMMITMENT_PERIOD_LABELS: Record<CommitmentPeriod, string> = {
  [CommitmentPeriod.CP0]: 'CP0',
  [CommitmentPeriod.CP1]: 'CP1',
  [CommitmentPeriod.CP2]: 'CP2',
  [CommitmentPeriod.CP3]: 'CP3',
};
