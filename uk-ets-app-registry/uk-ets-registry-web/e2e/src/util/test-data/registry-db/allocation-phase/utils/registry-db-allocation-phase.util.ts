import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { RegistryDbAllocationPhase } from '../model/registry-db-allocation-phase';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbAllocationPhaseTestData {
  static sequenceId = 100000001;

  static nextId() {
    return this.sequenceId++;
  }

  static resetIndices() {
    try {
      this.sequenceId = 100000001;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async deleteAllocationPhaseInDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ALLOCATION_PHASE_PATH,
      PathsAndTypesRegistryDb.ALLOCATION_PHASE_TYPE
    );
  }

  static async insertAllocationPhase(
    allocationPhases: RegistryDbAllocationPhase[]
  ) {
    const createdAllocationPhases: RegistryDbAllocationPhase[] = [];
    for (const allocationPhase of allocationPhases) {
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ALLOCATION_PHASE_PATH,
          PathsAndTypesRegistryDb.ALLOCATION_PHASE_TYPE,
          allocationPhase
        );
        createdAllocationPhases.push(allocationPhase);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAllocationPhases;
  }

  static async updateAllocationPhaseInDB(
    allocationPhases: RegistryDbAllocationPhase[]
  ) {
    const createdAllocationPhases: RegistryDbAllocationPhase[] = [];
    for (const allocationPhase of allocationPhases) {
      await PostgrestClient.genericUpdate(
        environment().postgrestRegistryBaseUrl,
        PathsAndTypesRegistryDb.ALLOCATION_PHASE_PATH +
          `?id=eq.${allocationPhase.id}`,
        PathsAndTypesRegistryDb.ALLOCATION_PHASE_TYPE,
        allocationPhase
      );

      try {
        createdAllocationPhases.push(allocationPhase);
        this.resetIndices();
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAllocationPhases;
  }
}
