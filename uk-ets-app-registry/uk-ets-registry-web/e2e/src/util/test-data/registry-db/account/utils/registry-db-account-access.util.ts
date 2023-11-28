import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { RegistryDbAccountAccess } from '../model/account-access/registry-db-account-access';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbAccountAccessTestData {
  static accountAccessSeqId = 100000001;

  static nextAccountAccessSeqId() {
    return this.accountAccessSeqId++;
  }

  static resetIndices() {
    try {
      this.accountAccessSeqId = 100000001;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadAccountAccesses(accountAccesses: RegistryDbAccountAccess[]) {
    const createdAccountAccesses: RegistryDbAccountAccess[] = [];
    for (const accountAccess of accountAccesses) {
      accountAccess.id = String(this.nextAccountAccessSeqId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ACCOUNT_ACCESS_PATH,
          PathsAndTypesRegistryDb.ACCOUNT_ACCESS_TYPE,
          accountAccess
        );
        createdAccountAccesses.push(accountAccess);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAccountAccesses;
  }

  static async deleteAllAccountAccessesFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ACCOUNT_ACCESS_PATH,
      PathsAndTypesRegistryDb.ACCOUNT_ACCESS_TYPE
    );
  }
}
