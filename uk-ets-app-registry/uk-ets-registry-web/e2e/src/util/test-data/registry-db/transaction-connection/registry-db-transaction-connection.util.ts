import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class RegistryDbTransactionConnectionTestData {
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

  static async deleteAllTransactionConnectionFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.TRANSACTION_CONNECTION_PATH,
      PathsAndTypesRegistryDb.TRANSACTION_CONNECTION_TYPE
    );
  }
}
