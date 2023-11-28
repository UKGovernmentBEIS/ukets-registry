import { RegistryDbRegisterLevel } from '../model/registry-level/registry-db-register-level';
import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { environment } from '../../../../environment-configuration';
import { PostgrestClient } from '../../../postgrest-client.util';

export class RegistryDbRegistryLevelTestData {
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

  static async loadRegistryLevelInDB(
    registerLevels: RegistryDbRegisterLevel[]
  ) {
    const createdRegistryLevels: RegistryDbRegisterLevel[] = [];
    for (const registryLevel of registerLevels) {
      registryLevel.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.REGISTRY_LEVEL_PATH,
          PathsAndTypesRegistryDb.REGISTRY_LEVEL_TYPE,
          registryLevel
        );
        createdRegistryLevels.push(registryLevel);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdRegistryLevels;
  }

  static async deleteAllRegistryLevelsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.REGISTRY_LEVEL_PATH,
      PathsAndTypesRegistryDb.REGISTRY_LEVEL_TYPE
    );
  }
}
