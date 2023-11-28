import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbUserFilesTestData {
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

  static async deleteAllUserFilesFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.USER_FILES_PATH,
      PathsAndTypesRegistryDb.USER_FILES_TYPE
    );
  }
}
