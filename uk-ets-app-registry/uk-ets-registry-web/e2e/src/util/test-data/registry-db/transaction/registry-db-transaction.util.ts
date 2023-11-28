import { RegistryDbTransaction } from './model/transaction/registry-db-transaction';
import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class RegistryDbTransactionTestData {
  static sequenceId = 100000001;
  static sequenceIdentifier = 10000000;

  static nextId() {
    return this.sequenceId++;
  }

  static nextTransactionIdentifier() {
    return this.sequenceIdentifier++;
  }

  static resetIndices() {
    try {
      this.sequenceId = 100000001;
      this.sequenceIdentifier = 10000000;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadTransactionsInDB(transactions: RegistryDbTransaction[]) {
    const createdTransactions: RegistryDbTransaction[] = [];
    for (const transaction of transactions) {
      transaction.id = String(this.nextId());

      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.TRANSACTION_PATH,
          PathsAndTypesRegistryDb.TRANSACTION_TYPE,
          transaction
        );
        createdTransactions.push(transaction);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdTransactions;
  }

  static async deleteAllTransactionsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.TRANSACTION_PATH,
      PathsAndTypesRegistryDb.TRANSACTION_TYPE
    );
  }
}
