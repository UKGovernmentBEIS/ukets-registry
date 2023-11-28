import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class TransactionLogDbReconciliationTestData {
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
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.RECONCILIATION_PATH,
      PathsAndTypesTransactionLogDb.RECONCILIATION_TYPE
    );
  }
}
