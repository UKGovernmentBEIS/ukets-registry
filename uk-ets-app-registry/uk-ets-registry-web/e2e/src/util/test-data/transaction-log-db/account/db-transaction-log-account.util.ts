import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { DbTransactionLogAccount } from './model/db-transaction-log-account';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class TransactionLogDbAccountTestData {
  static sequenceId = 1;

  static nextId() {
    return this.sequenceId++;
  }

  // in case of new project build, transaction logs database includes by default accounts UK Total Quantity Account, UK Auction Account etc.
  // all these accounts are deleted and in case of such accounts need in scenarios, they will be created by bdd respective steps.

  static resetIndices() {
    try {
      this.sequenceId = 1;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadAccounts(accounts: DbTransactionLogAccount[]) {
    const createdAccounts: DbTransactionLogAccount[] = [];
    for (const account of accounts) {
      account.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestTransactionLogBaseUrl,
          PathsAndTypesTransactionLogDb.ACCOUNT_PATH,
          PathsAndTypesTransactionLogDb.ACCOUNT_TYPE,
          account
        );
        createdAccounts.push(account);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAccounts;
  }

  static async deleteAllAccountFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.ACCOUNT_PATH,
      PathsAndTypesTransactionLogDb.ACCOUNT_TYPE
    );
  }
}
