import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class TransactionLogDbTransactionBlockTestData {
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

  static async deleteAllTransactionBlockFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.TRANSACTION_BLOCK_PATH,
      PathsAndTypesTransactionLogDb.TRANSACTION_BLOCK_TYPE
    );
  }
}
