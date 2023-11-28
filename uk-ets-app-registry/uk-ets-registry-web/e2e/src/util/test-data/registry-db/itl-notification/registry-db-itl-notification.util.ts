import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class RegistryDbItlNotificationTestData {
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

  static async deleteAllItlNotificationFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ITL_NOTIFICATION_PATH,
      PathsAndTypesRegistryDb.ITL_NOTIFICATION_TYPE
    );
  }
}
