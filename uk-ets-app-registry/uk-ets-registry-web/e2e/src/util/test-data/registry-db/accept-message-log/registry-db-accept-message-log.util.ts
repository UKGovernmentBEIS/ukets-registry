import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class RegistryDbAcceptMessageLogTestData {
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

  static async deleteAllAcceptMessageLogFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ACCEPT_MESSAGE_LOG_PATH,
      PathsAndTypesRegistryDb.ACCEPT_MESSAGE_LOG_TYPE
    );
  }
}
