import { Given } from 'cucumber';
import { RegistryDbAllocationPhaseBuilder } from '../model/registry-db-allocation-phase.builder';
import { RegistryDbAllocationPhaseTestData } from './registry-db-allocation-phase.util';

Given(
  'The following allocation phase have been set with initial {string}, consumed {string}, pending {string}',
  async (initial: string, consumed: string, pending: string) => {
    console.log(
      'The following allocation phase have been set with initial ${initial}, consumed ${consumed}, pending ${pending}'
    );
    const allocationPhases = [];
    allocationPhases.push(
      new RegistryDbAllocationPhaseBuilder()
        .initial_phase_cap(initial)
        .consumed_phase_cap(consumed)
        .pending_phase_cap(pending)
        .build()
    );
    await RegistryDbAllocationPhaseTestData.updateAllocationPhaseInDB(
      allocationPhases
    );
  }
);
