import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class RegistryDbTransactionResponseTestData {
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

  static async deleteAllTransactionResponseFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.TRANSACTION_RESPONSE_PATH,
      PathsAndTypesRegistryDb.TRANSACTION_RESPONSE_TYPE
    );
  }
}
