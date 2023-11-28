import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';
import { RegistryDbAllocationStatus } from './model/registry-db-allocation-status';

export class RegistryDbAllocationStatusTestData {
  static sequenceId = 1;

  static nextId() {
    return this.sequenceId++;
  }

  static resetIndices() {
    try {
      this.sequenceId = 1;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadAllocationStatus(
    allocationStat: RegistryDbAllocationStatus[]
  ) {
    const createdAllocationStatus: RegistryDbAllocationStatus[] = [];
    for (const allocationStatus of allocationStat) {
      try {
        allocationStatus.id = String(this.nextId());
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ALLOCATION_STATUS_PATH,
          PathsAndTypesRegistryDb.ALLOCATION_STATUS_TYPE,
          allocationStatus
        );
        createdAllocationStatus.push(allocationStatus);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAllocationStatus;
  }

  static async deleteAllocationStatusInDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ALLOCATION_STATUS_PATH,
      PathsAndTypesRegistryDb.ALLOCATION_STATUS_TYPE
    );
  }
}
