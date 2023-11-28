import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class TransactionLogDbDatabaseChangeLogLockTestData {
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

  static async deleteAllDatabaseChangeLogLockFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.DATABASE_CHANGE_LOG_LOCK_PATH,
      PathsAndTypesTransactionLogDb.DATABASE_CHANGE_LOG_LOCK_TYPE
    );
  }
}
