import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class TransactionLogDbReconciliationHistoryTestData {
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

  static async deleteAllReconciliationHistoryFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.RECONCILIATION_HISTORY_PATH,
      PathsAndTypesTransactionLogDb.RECONCILIATION_HISTORY_TYPE
    );
  }
}
