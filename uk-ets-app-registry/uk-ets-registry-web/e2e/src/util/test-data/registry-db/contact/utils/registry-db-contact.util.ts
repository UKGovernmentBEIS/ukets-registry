import { RegistryDbContact } from '../model/registry-db-contact';
import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbContactTestData {
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

  static async loadContactsInDB(contacts: RegistryDbContact[]) {
    const createdContacts: RegistryDbContact[] = [];
    for (const contact of contacts) {
      contact.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.CONTACT_PATH,
          PathsAndTypesRegistryDb.CONTACT_TYPE,
          contact
        );
        createdContacts.push(contact);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdContacts;
  }

  static async deleteAllContactsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.CONTACT_PATH,
      PathsAndTypesRegistryDb.CONTACT_TYPE
    );
  }
}
