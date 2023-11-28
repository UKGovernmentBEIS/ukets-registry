import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { RegistryDbAccountHolderRepresentative } from '../model/registry-db-account-holder-representative';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbAccountHolderRepTestData {
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

  static async loadAccountHolderReps(
    accountHolderReps: RegistryDbAccountHolderRepresentative[]
  ) {
    const createdAccountHolderReps: RegistryDbAccountHolderRepresentative[] = [];
    for (const accountHolderRep of accountHolderReps) {
      accountHolderRep.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ACCOUNT_HOLDER_REP_PATH,
          PathsAndTypesRegistryDb.ACCOUNT_HOLDER_REP_TYPE,
          accountHolderRep
        );
        createdAccountHolderReps.push(accountHolderRep);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAccountHolderReps;
  }

  static async deleteAllAccountHolderRepsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ACCOUNT_HOLDER_REP_PATH,
      PathsAndTypesRegistryDb.ACCOUNT_HOLDER_REP_TYPE
    );
  }
}
