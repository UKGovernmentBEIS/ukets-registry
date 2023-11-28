import { Given, TableDefinition } from 'cucumber';
import { RegistryDbRegistryLevelBuilder } from './model/registry-level/registry-db-registry-level.builder';
import { RegistryDbRegistryLevelTestData } from './utils/registry-db-registry-level.util';

Given(
  'I have the following registry levels',
  async (dataTable: TableDefinition) => {
    const rows: string[][] = dataTable.raw();

    let registryLevelType = '';
    let registryLevelUnitType = '';
    let registryLevelCommitmentPeriod = '';
    let registryLevelEnvironmentalActivity = '';
    let registryLevelInitial = '';
    let registryLevelConsumed = '';
    let registryLevelPending = '';

    for (const row of rows) {
      if (row[0] === 'fieldName') {
        continue;
      }
      switch (row[0]) {
        case 'Type': {
          registryLevelType = row[1];
          break;
        }
        case 'UnitType': {
          registryLevelUnitType = row[1];
          break;
        }
        case 'CommitmentPeriod': {
          registryLevelCommitmentPeriod = row[1];
          break;
        }
        case 'EnvironmentalActivity': {
          registryLevelEnvironmentalActivity = row[1];
          break;
        }
        case 'Initial': {
          registryLevelInitial = row[1];
          break;
        }
        case 'Consumed': {
          registryLevelConsumed = row[1];
          break;
        }
        case 'Pending': {
          registryLevelPending = row[1];
          break;
        }
      }
    }

    /**
     * Build Registry Level
     */
    const registryLevel = new RegistryDbRegistryLevelBuilder()
      .type(registryLevelType === '' ? null : registryLevelType)
      .unitType(registryLevelUnitType === '' ? null : registryLevelUnitType)
      .commitmentPeriod(
        registryLevelCommitmentPeriod === ''
          ? null
          : registryLevelCommitmentPeriod
      )
      .environmentalActivity(
        registryLevelEnvironmentalActivity === 'not defined'
          ? null
          : registryLevelEnvironmentalActivity
      )
      .initial(registryLevelInitial === '' ? null : registryLevelInitial)
      .consumed(registryLevelConsumed === '' ? null : registryLevelConsumed)
      .pending(registryLevelPending === '' ? null : registryLevelPending)
      .build();
    await RegistryDbRegistryLevelTestData.loadRegistryLevelInDB([
      registryLevel,
    ]);
  }
);
