import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class TransactionLogDbDatabaseChangeLogTestData {
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

  static async deleteAllDatabaseChangeLogFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.DATABASE_CHANGE_LOG_PATH,
      PathsAndTypesTransactionLogDb.DATABASE_CHANGE_LOG_TYPE
    );
  }
}
