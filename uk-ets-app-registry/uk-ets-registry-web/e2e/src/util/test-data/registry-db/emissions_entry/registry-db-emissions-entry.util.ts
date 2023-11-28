import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';
import { RegistryDbUnitBlock } from '../transaction/model/unit-block/registry-db-unit-block';
import { DbEmissionsEntry } from './registry-db-emissions-entry';

export class RegistryDbEmissionsEntryTestData {
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

  static async loadEmissionsEntriesInDB(emissionsEntry: DbEmissionsEntry[]) {
    const createdEmissionsEntries: DbEmissionsEntry[] = [];
    for (const emissions of emissionsEntry) {
      emissions.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.EMISSIONS_ENTRY_PATH,
          PathsAndTypesRegistryDb.EMISSIONS_ENTRY_TYPE,
          emissions
        );
        createdEmissionsEntries.push(emissions);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdEmissionsEntries;
  }

  static async deleteAllEmissionsEntryFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.EMISSIONS_ENTRY_PATH,
      PathsAndTypesRegistryDb.EMISSIONS_ENTRY_TYPE
    );
  }
}
