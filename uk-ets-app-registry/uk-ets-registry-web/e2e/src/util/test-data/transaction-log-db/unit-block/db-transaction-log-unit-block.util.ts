import { PathsAndTypesTransactionLogDb } from '../paths-and-types-transaction-log-db';
import { DbTransactionLogUnitBlock } from './model/db-transaction-log-unit-block';
import { environment } from '../../../environment-configuration';
import { PostgrestClient } from '../../postgrest-client.util';

export class TransactionLogDbUnitBlockTestData {
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

  static async loadUnitBlocks(unitBlocks: DbTransactionLogUnitBlock[]) {
    const createdUnitBlocks: DbTransactionLogUnitBlock[] = [];
    for (const unitBlock of unitBlocks) {
      unitBlock.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestTransactionLogBaseUrl,
          PathsAndTypesTransactionLogDb.UNIT_BLOCK_PATH,
          PathsAndTypesTransactionLogDb.UNIT_BLOCK_TYPE,
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
      environment().postgrestTransactionLogBaseUrl,
      PathsAndTypesTransactionLogDb.UNIT_BLOCK_PATH,
      PathsAndTypesTransactionLogDb.UNIT_BLOCK_TYPE
    );
  }
}
