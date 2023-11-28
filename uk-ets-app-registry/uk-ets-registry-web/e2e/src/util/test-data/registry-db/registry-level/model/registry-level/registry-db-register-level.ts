import { RegistryDbRegistryLevelBuilder } from './registry-db-registry-level.builder';

export class RegistryDbRegisterLevel {
  id: string;
  type: string;
  unit_type: string;
  commitment_period: string;
  environmental_activity: string;
  initial: string;
  consumed: string;
  pending: string;

  constructor(builder: RegistryDbRegistryLevelBuilder) {
    this.type = builder.getType();
    this.unit_type = builder.getUnitType();
    this.commitment_period = builder.getCommitmentPeriod();
    this.environmental_activity = builder.getEnvironmentalActivity();
    this.initial = builder.getInitial();
    this.consumed = builder.getConsumed();
    this.pending = builder.getPending();
  }
}
