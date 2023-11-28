import { RegistryDbUnitBlock } from './model/unit-block/registry-db-unit-block';
import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class RegistryDbUnitBlockTestData {
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

  static async loadUnitBlocksInDB(unitBlocks: RegistryDbUnitBlock[]) {
    const createdUnitBlocks: RegistryDbUnitBlock[] = [];
    for (const unitBlock of unitBlocks) {
      unitBlock.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.UNIT_BLOCK_PATH,
          PathsAndTypesRegistryDb.UNIT_BLOCK_TYPE,
          unitBlock
        );
        createdUnitBlocks.push(unitBlock);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdUnitBlocks;
  }

  static async deleteAllUnitBlocksFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.UNIT_BLOCK_PATH,
      PathsAndTypesRegistryDb.UNIT_BLOCK_TYPE
    );
  }
}
