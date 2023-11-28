import { Given, TableDefinition } from 'cucumber';
import { assert } from 'chai';
import { RegistryDbEtsTransactionsTestData } from './registry-db-ets-transactions.util';
import { DbAllocationYearBuilder } from '../model/registry-db-ets-transactions.builder';

Given(
  'The following issuance limits have been set:',
  async (dataTable: TableDefinition) => {
    console.log('Executing step: The following issuance limits have been set:');
    const rows: string[][] = dataTable.raw();

    const years = [];
    const issuance_caps = [];
    let counter = 0;
    const allocationYears = [];
    let dbId = 100000001;

    for (const row of rows) {
      // gherkin datatable headers
      if (counter === 0) {
        assert.isTrue(row[0] === 'year' && row[1] === 'issuance_cap');
        console.log(`| ${row[0]} | ${row[1]} |`);
      }
      // gherkin datatable actual data
      else {
        years[counter - 1] = row[0];
        issuance_caps[counter - 1] = row[1];
        console.log(`| ${row[0]} | ${row[1]}        |`);
        allocationYears.push(
          new DbAllocationYearBuilder()
            .id(dbId.toString())
            .year(years[counter - 1])
            .initial_yearly_cap(issuance_caps[counter - 1])
            .build()
        );
        dbId++;
      }
      counter++;
    }
    await RegistryDbEtsTransactionsTestData.updateAllocationYearsInDB(
      allocationYears
    );
  }
);
