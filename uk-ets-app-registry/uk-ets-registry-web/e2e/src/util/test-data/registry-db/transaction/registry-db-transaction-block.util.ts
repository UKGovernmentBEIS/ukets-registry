import { RegistryDbTransactionBlock } from './model/transaction-block/registry-db-transaction-block';
import { PathsAndTypesRegistryDb } from '../paths-and-types-registry-db';
import { PostgrestClient } from '../../postgrest-client.util';
import { environment } from '../../../environment-configuration';

export class RegistryDbTransactionBlockTestData {
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

  static async loadTransactionBlocksInDB(
    transactionBlocks: RegistryDbTransactionBlock[]
  ) {
    const createdTransactionBlocks: RegistryDbTransactionBlock[] = [];
    for (const transactionBlock of transactionBlocks) {
      transactionBlock.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.TRANSACTION_BLOCK_PATH,
          PathsAndTypesRegistryDb.TRANSACTION_BLOCK_TYPE,
          transactionBlock
        );
        createdTransactionBlocks.push(transactionBlock);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdTransactionBlocks;
  }

  static async deleteAllTransactionBlocksFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.TRANSACTION_BLOCK_PATH,
      PathsAndTypesRegistryDb.TRANSACTION_BLOCK_TYPE
    );
  }
}
