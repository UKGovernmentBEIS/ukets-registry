import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { RegistryDbTrustedAccount } from '../model/registry-db-trusted-account';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbTrustedRegistryDbAccountTestData {
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

  static async loadTrustedAccountsInDB(
    trustedAccounts: RegistryDbTrustedAccount[]
  ) {
    const createdTrustedAccounts: RegistryDbTrustedAccount[] = [];
    for (const trustedAccount of trustedAccounts) {
      trustedAccount.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.TRUSTED_ACCOUNT_PATH,
          PathsAndTypesRegistryDb.TRUSTED_ACCOUNT_TYPE,
          trustedAccount
        );
        createdTrustedAccounts.push(trustedAccount);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdTrustedAccounts;
  }

  static async deleteAllTrustedAccountsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.TRUSTED_ACCOUNT_PATH,
      PathsAndTypesRegistryDb.TRUSTED_ACCOUNT_TYPE
    );
  }
}
