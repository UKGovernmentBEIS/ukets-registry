import { RegistryDbAllocationPhaseBuilder } from './registry-db-allocation-phase.builder';

export class RegistryDbAllocationPhase {
  id: string;
  code: string;
  initial_phase_cap: string;
  consumed_phase_cap: string;
  pending_phase_cap: string;

  constructor(builder: RegistryDbAllocationPhaseBuilder) {
    this.id = builder.getId;
    this.code = builder.getCode;
    this.initial_phase_cap = builder.getInitialPhaseCap;
    this.consumed_phase_cap = builder.getConsumedPhaseCap;
    this.pending_phase_cap = builder.getPendingPhaseCap;
  }
}
