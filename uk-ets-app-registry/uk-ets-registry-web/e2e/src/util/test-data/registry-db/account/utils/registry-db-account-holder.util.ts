import { RegistryDbAccountHolder } from '../model/account-holder/registry-db-account-holder';
import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { environment } from '../../../../environment-configuration';
import { PostgrestClient } from '../../../postgrest-client.util';
import { assert } from 'chai';

export class RegistryDbAccountHolderTestData {
  static accountHolderSeqId = 100000001;
  static accountHolderSeqIdentifier = 100001;

  static nextAccountHolderSeqId() {
    return this.accountHolderSeqId++;
  }

  static nextAccountHolderSeqIdentifier() {
    return this.accountHolderSeqIdentifier++;
  }

  static resetIndices() {
    try {
      this.accountHolderSeqId = 100000001;
      this.accountHolderSeqIdentifier = 100001;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadAccountHolders(holders: RegistryDbAccountHolder[]) {
    const createdHolders: RegistryDbAccountHolder[] = [];
    for (const holder of holders) {
      holder.id = String(this.nextAccountHolderSeqId());
      holder.identifier = String(this.nextAccountHolderSeqIdentifier());
      console.log(`account holder type: '${holder.type}'.`);
      if (holder.type === 'INDIVIDUAL') {
        holder.first_name = holder.type.toLowerCase() + ' first name 1';
        holder.last_name = holder.type.toLowerCase() + ' last name 1';
      } else if (holder.type === 'ORGANISATION') {
        console.log(
          `first and last name will not be set because the holder type is ORGANISATION.`
        );
      } else if (holder.type === 'GOVERNMENT') {
        console.log(
          `first and last name will not be set because the holder type is GOVERNMENT.`
        );
      } else {
        assert.fail(`wrong account holder type: '${holder.type}'.`);
      }
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ACCOUNT_HOLDER_PATH,
          PathsAndTypesRegistryDb.ACCOUNT_HOLDER_TYPE,
          holder
        );
        createdHolders.push(holder);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdHolders;
  }

  static async deleteAllAccountHoldersFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ACCOUNT_HOLDER_PATH,
      PathsAndTypesRegistryDb.ACCOUNT_HOLDER_TYPE
    );
  }
}
