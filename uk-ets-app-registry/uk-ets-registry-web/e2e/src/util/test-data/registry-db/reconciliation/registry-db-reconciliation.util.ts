import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class RegistryDbReconciliationTestData {
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

  static async deleteAllReconciliationsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.RECONCILIATION_PATH,
      PathsAndTypesRegistryDb.RECONCILIATION_TYPE
    );
  }
}
