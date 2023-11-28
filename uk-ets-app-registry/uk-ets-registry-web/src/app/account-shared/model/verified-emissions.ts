export type Excluded = 'Excluded';
// eslint-disable-next-line @typescript-eslint/consistent-type-definitions

export interface VerifiedEmissionsResult {
  readonly verifiedEmissions: VerifiedEmissions[];
  readonly lastYearOfVerifiedEmissions?: number;
}

export interface VerifiedEmissions {
  readonly compliantEntityId: number;
  readonly year: number;
  readonly reportableEmissions: number | Excluded;
  readonly lastUpdated: Date;
}
