import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';
import { RegistryDbInstallationOwnership } from '../installation-ownership/model/registry-db-installation-ownership';
import { RegistryDbAccountOwnership } from './model/registry-db-account-ownership';

export class RegistryDbAccountOwnershipTestData {
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

  static async loadAccountOwnershipInDb(
    ownerships: RegistryDbAccountOwnership[]
  ) {
    const accountOwnerships: RegistryDbAccountOwnership[] = [];

    for (const accountOwnership of ownerships) {
      accountOwnership.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ACCOUNT_OWNERSHIP_PATH,
          PathsAndTypesRegistryDb.ACCOUNT_OWNERSHIP_TYPE,
          accountOwnership
        );
        accountOwnerships.push(accountOwnership);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return accountOwnerships;
  }

  static async deleteAllAccountOwnershipFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ACCOUNT_OWNERSHIP_PATH,
      PathsAndTypesRegistryDb.ACCOUNT_OWNERSHIP_TYPE
    );
  }
}
