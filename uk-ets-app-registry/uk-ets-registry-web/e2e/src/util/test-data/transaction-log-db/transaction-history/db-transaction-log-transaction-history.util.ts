import { PostgrestClient } from '../../postgrest-client.util';
import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { environment } from '../../../environment-configuration';

export class TransactionLogDbTransactionHistoryTestData {
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

  static async deleteAllTransactionHistoryFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.TRANSACTION_HISTORY_PATH,
      PathsAndTypesTransactionLogDb.TRANSACTION_HISTORY_TYPE
    );
  }
}
