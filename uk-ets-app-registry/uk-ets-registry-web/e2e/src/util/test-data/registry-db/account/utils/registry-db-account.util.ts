import { RegistryDbAccount } from '../model/account/registry-db-account';
import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { environment } from '../../../../environment-configuration';
import { PostgrestClient } from '../../../postgrest-client.util';

export class RegistryDbAccountTestData {
  static accountSeqId = 100000001;

  static nextAccountSeqId() {
    return this.accountSeqId++;
  }

  static resetIndices() {
    try {
      this.accountSeqId = 100000001;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadAccounts(accounts: RegistryDbAccount[]) {
    const createdAccounts: RegistryDbAccount[] = [];
    for (const account of accounts) {
      account.id = String(this.nextAccountSeqId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ACCOUNT_PATH,
          PathsAndTypesRegistryDb.ACCOUNT_TYPE,
          account
        );
        createdAccounts.push(account);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAccounts;
  }

  static async deleteAllAccountsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ACCOUNT_PATH,
      PathsAndTypesRegistryDb.ACCOUNT_TYPE
    );
  }
}
