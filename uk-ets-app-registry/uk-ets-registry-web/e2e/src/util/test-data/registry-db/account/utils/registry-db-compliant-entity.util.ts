import { RegistryDbCompliantEntity } from '../model/compliant-entity/registry-db-compliant-entity';
import { RegistryDbInstallation } from '../model/compliant-entity/registry-db-installation';
import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { CompliantEntityType } from '../model/types/registry-db-compliant-entity.enum';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbCompliantEntityTestData {
  static compliantEntitySeqId = 100000001;
  static compliantEntitySeqIdentifier = 100001;

  static nextCompliantEntitySeqId() {
    return this.compliantEntitySeqId++;
  }

  static nextCompliantEntitySeqIdentifier() {
    return this.compliantEntitySeqIdentifier++;
  }

  static resetIndices() {
    try {
      this.compliantEntitySeqId = 100000001;
      this.compliantEntitySeqIdentifier = 100001;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadCompliantEntities(
    entities: [RegistryDbCompliantEntity, any][],
    type: string
  ) {
    const createdEntities: [
      RegistryDbCompliantEntity,
      RegistryDbInstallation
    ][] = [];
    for (const entity of entities) {
      entity[0].id = String(this.nextCompliantEntitySeqId());
      entity[0].identifier = String(this.nextCompliantEntitySeqIdentifier());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.COMPLIANT_ENTITY_PATH,
          PathsAndTypesRegistryDb.COMPLIANT_ENTITY_TYPE,
          entity[0]
        );
        entity[1].compliant_entity_id = entity[0].id;
        console.log('CompliantEntityType: ' + type);
        if (type === CompliantEntityType.INSTALLATION) {
          try {
            await PostgrestClient.genericLoad(
              environment().postgrestRegistryBaseUrl,
              PathsAndTypesRegistryDb.INSTALLATION_PATH,
              PathsAndTypesRegistryDb.INSTALLATION_TYPE,
              entity[1],
              'compliant_entity_id'
            );
            createdEntities.push(entity);
          } catch (e) {
            console.error(`exception in test data: '${e}'.`);
          }
        } else {
          try {
            await PostgrestClient.genericLoad(
              environment().postgrestRegistryBaseUrl,
              PathsAndTypesRegistryDb.AIRCRAFT_OPERATOR_PATH,
              PathsAndTypesRegistryDb.AIRCRAFT_OPERATOR_TYPE,
              entity[1],
              'compliant_entity_id'
            );
            createdEntities.push(entity);
          } catch (e) {
            console.error(`exception in test data: '${e}'.`);
          }
        }
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdEntities;
  }

  static async deleteAllCompliantEntitiesFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.INSTALLATION_PATH,
      PathsAndTypesRegistryDb.INSTALLATION_TYPE
      // ,
      // '1',
      // true,
      // 'compliant_entity_id'
    );
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.AIRCRAFT_OPERATOR_PATH,
      PathsAndTypesRegistryDb.AIRCRAFT_OPERATOR_TYPE
      // ,
      // '1',
      // true,
      // 'compliant_entity_id'
    );
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.COMPLIANT_ENTITY_PATH,
      PathsAndTypesRegistryDb.COMPLIANT_ENTITY_TYPE
      // ,
      // '1',
      // true
    );
  }
}
