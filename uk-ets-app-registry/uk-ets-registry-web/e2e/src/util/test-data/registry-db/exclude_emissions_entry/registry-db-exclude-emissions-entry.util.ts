import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class RegistryDbExcludeEmissionsEntryTestData {
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

  static async deleteAllExcludeEmissionsEntryFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.EXCLUDE_EMISSIONS_ENTRY_PATH,
      PathsAndTypesRegistryDb.EXCLUDE_EMISSIONS_ENTRY_TYPE
    );
  }
}
