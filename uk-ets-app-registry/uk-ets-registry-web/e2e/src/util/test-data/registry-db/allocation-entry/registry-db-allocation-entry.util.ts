import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { RegistryDbAllocationEntry } from './model/registry-db-allocation-entry';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class RegistryDbAllocationEntryTestData {
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

  static async loadAllocationEntry(
    allocationEntries: RegistryDbAllocationEntry[]
  ) {
    const createdAllocationEntry: RegistryDbAllocationEntry[] = [];
    for (const allocationEntry of allocationEntries) {
      allocationEntry.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ALLOCATION_ENTRY_PATH,
          PathsAndTypesRegistryDb.ALLOCATION_ENTRY_TYPE,
          allocationEntry
        );
        createdAllocationEntry.push(allocationEntry);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAllocationEntry;
  }

  static async deleteAllocationEntryInDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ALLOCATION_ENTRY_PATH,
      PathsAndTypesRegistryDb.ALLOCATION_ENTRY_TYPE
    );
  }
}
