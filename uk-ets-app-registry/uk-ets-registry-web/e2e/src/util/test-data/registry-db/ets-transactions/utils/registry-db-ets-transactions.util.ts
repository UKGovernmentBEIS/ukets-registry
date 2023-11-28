import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { DbAllocationYear } from '../model/registry-db-ets-transactions';
import { DbAllocationYearBuilder } from '../model/registry-db-ets-transactions.builder';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbEtsTransactionsTestData {
  static sequenceId = 100000001;

  static resetIndices() {
    try {
      this.sequenceId = 100000001;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async restoreAllocationYearsInDB() {
    try {
      // define default values using a years range from 2021 to 2030:
      const defaultAllocationYears = [];
      let dbId = 100000001;
      for (let year = 2021; year < 2031; year++) {
        defaultAllocationYears.push(
          new DbAllocationYearBuilder()
            .id(dbId.toString())
            .year(year.toString())
            .initial_yearly_cap('100000')
            .build()
        );
        dbId++;
      }

      await this.deleteAllAllocationYearsFromDB();
      await this.loadAllocationYearsInDB(defaultAllocationYears);
    } catch (e) {
      console.error(`exception in test data: '${e}'.`);
    }
  }

  static async updateAllocationYearsInDB(allocationYears: DbAllocationYear[]) {
    const createdAllocationYears: DbAllocationYear[] = [];
    for (const allocationYear of allocationYears) {
      await PostgrestClient.genericUpdate(
        environment().postgrestRegistryBaseUrl,
        PathsAndTypesRegistryDb.ALLOCATION_YEAR_PATH +
          `?id=eq.${allocationYear.id}`,
        PathsAndTypesRegistryDb.ALLOCATION_YEAR_TYPE,
        allocationYear
      );

      try {
        createdAllocationYears.push(allocationYear);
        this.resetIndices();
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAllocationYears;
  }

  static async loadAllocationYearsInDB(allocationYears: DbAllocationYear[]) {
    const createdAllocationYears: DbAllocationYear[] = [];
    for (const allocationYear of allocationYears) {
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.ALLOCATION_YEAR_PATH,
          PathsAndTypesRegistryDb.ALLOCATION_YEAR_TYPE,
          allocationYear
        );
        createdAllocationYears.push(allocationYear);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdAllocationYears;
  }

  static async deleteAllAllocationYearsFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.ALLOCATION_YEAR_PATH,
      PathsAndTypesRegistryDb.ALLOCATION_YEAR_TYPE
    );
  }
}
