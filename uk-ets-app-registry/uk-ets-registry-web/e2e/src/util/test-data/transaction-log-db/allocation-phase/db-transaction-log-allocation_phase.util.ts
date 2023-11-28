import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class TransactionLogDbAllocationPhaseTestData {
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

  static async deleteAllAllocationPhaseFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.ALLOCATION_PHASE_PATH,
      PathsAndTypesTransactionLogDb.ALLOCATION_PHASE_TYPE
    );
  }
}
