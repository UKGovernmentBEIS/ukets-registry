import { Given, TableDefinition } from 'cucumber';
import { RegistryDbRegistryLevelBuilder } from '../model/registry-level/registry-db-registry-level.builder';
import { getEnvironmentalActivity } from '../model/types/registry-db-envirnomental-activity.enum';
import { getRegistryLevelType } from '../model/types/registry-db-registry-level-type.enum';
import { getUnitType } from '../../common/registry-db-unit-type.enum';
import { RegistryDbRegistryLevelTestData } from './registry-db-registry-level.util';
import { getCommitmentPeriod } from '../../common/registry-db-commitment-period.enum';

Given(
  'the following issuance limits and units have been set for the current commitment period',
  async (dataTable: TableDefinition) => {
    console.log(
      `Executing step: the following issuance limits and units have been set for the current commitment period`
    );

    const rows: string[][] = dataTable.raw();

    for (const row of rows) {
      console.log(`| ${row} |`);

      if (row[0] === 'period') {
        continue;
      }

      const commitmentPeriod = row[0];
      const unitType = row[1];
      const quantity = row[2];
      const environmentalActivity = row[3];
      const consumed = row[4] === '' ? '0' : row[4];

      const registryLevel = new RegistryDbRegistryLevelBuilder()
        .type(getRegistryLevelType('Kyoto Issuance'))
        .unitType(getUnitType(unitType))
        .commitmentPeriod(getCommitmentPeriod(commitmentPeriod))
        .environmentalActivity(getEnvironmentalActivity(environmentalActivity))
        .initial(quantity)
        .consumed(consumed)
        .pending('0')
        .build();
      await RegistryDbRegistryLevelTestData.loadRegistryLevelInDB([
        registryLevel,
      ]);
    }
  }
);
