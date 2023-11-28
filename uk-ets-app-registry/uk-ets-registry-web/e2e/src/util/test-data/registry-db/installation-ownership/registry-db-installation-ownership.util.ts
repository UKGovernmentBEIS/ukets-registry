import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';
import { RegistryDbInstallationOwnership } from './model/registry-db-installation-ownership';

export class RegistryDbInstallationOwnershipTestData {
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

  static async loadInstallationOwnershipInDb(
    ownerships: RegistryDbInstallationOwnership[]
  ) {
    const installationOwnerships: RegistryDbInstallationOwnership[] = [];

    for (const installationOwnership of ownerships) {
      installationOwnership.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.INSTALLATION_OWNERSHIP_PATH,
          PathsAndTypesRegistryDb.INSTALLATION_OWNERSHIP_TYPE,
          installationOwnership
        );
        installationOwnerships.push(installationOwnership);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return installationOwnerships;
  }

  static async deleteAllInstallationOwnershipFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.INSTALLATION_OWNERSHIP_PATH,
      PathsAndTypesRegistryDb.INSTALLATION_OWNERSHIP_TYPE
    );
  }
}
