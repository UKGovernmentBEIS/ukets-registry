import { Given, TableDefinition } from 'cucumber';
import { RegistryDbAccountHolderBuilder } from '../model/account-holder/registry-db-account-holder.builder';
import { RegistryDbContactBuilder } from '../../contact/model/registry-db-contact.builder';
import { RegistryDbContactTestData } from '../../contact/utils/registry-db-contact.util';
import { RegistryDbAccountHolderTestData } from './registry-db-account-holder.util';
import { RegistryDbCompliantEntityBuilder } from '../model/compliant-entity/registry-db-compliant-entity.builder';
import { RegistryDbInstallationBuilder } from '../model/compliant-entity/registry-db-installation.builder';
import { RegistryDbCompliantEntityTestData } from './registry-db-compliant-entity.util';
import { RegistryDbAircraftOperatorBuilder } from '../model/compliant-entity/registry-db-aircraft-operator.builder';
import { RegistryDbAccountHolder } from '../model/account-holder/registry-db-account-holder';
import {
  AOHA,
  OHA,
  PHA,
  PHA_2,
  TRADING,
} from '../model/data/registry-db-account-properties';
import { RegistryDbAccountTestData } from './registry-db-account.util';
import { RegistryDbCompliantEntity } from '../model/compliant-entity/registry-db-compliant-entity';
import moment from 'moment';
import { AccountType } from '../model/types/registry-db-account-type.enum';
import { CompliantEntityType } from '../model/types/registry-db-compliant-entity.enum';
import { browser } from 'protractor';
import { RegistryDbUnitBlockBuilder } from '../../transaction/model/unit-block/registry-db-unit-block.builder';
import { RegistryDbUnitBlockTestData } from '../../transaction/registry-db-unit-block.util';
import { KnowsThePage } from '../../../../knows-the-page.po';
import { convertToBoolean, delay } from '../../../../step.util';
import { RegistryDbRegistryAccountBuilder } from '../model/account/registry-db-registry-account.builder';
import * as assert from 'assert';
import { DbTransactionLogAccountBuilder } from '../../../transaction-log-db/account/model/db-transaction-log-account.builder';
import { TransactionLogDbAccountTestData } from '../../../transaction-log-db/account/db-transaction-log-account.util';
import { DbTransactionLogUnitBlockBuilder } from '../../../transaction-log-db/unit-block/model/db-transaction-log-unit-block.builder';
import { TransactionLogDbUnitBlockTestData } from '../../../transaction-log-db/unit-block/db-transaction-log-unit-block.util';

export function accountUkOrGb(account: string) {
  try {
    const accountPrefix = account.substr(0, 2);
    if (accountPrefix === 'UK' || accountPrefix === 'GB') {
      console.log(`Account is '${accountPrefix}'.`);
      return accountPrefix;
    } else {
      assert.fail(
        `account can be 'UK' or 'GB'. Currently found: '${accountPrefix}'.`
      );
    }
  } catch (e) {
    console.error(`exception in accountUkOrGb: '${e}'.`);
  }
}

Given(
  'the following accounts have been created',
  async (dataTable: TableDefinition) => {
    console.log(`Executing step: the following accounts have been created:`);
    let ohaIndex = 0;
    let aohaIndex = 0;
    let tradingIndex = 0;
    let personIndex = 0;
    await delay(1000);
    const rows: string[][] = dataTable.raw();

    for (const row of rows) {
      console.log(`| ${row} |`);

      if (row[0] === 'account_id') {
        continue;
      }

      const accountFullIdentifier = row[0];
      const accountPrefix = accountUkOrGb(accountFullIdentifier);
      const kyotoAccountType = row[1];
      const registryAccountType =
        row[2] === 'Null' || row[2] === '' || row[2] === undefined
          ? 'NONE'
          : row[2];
      const accountName = row[3];
      const accountStatus = row[4];
      const requestStatus = 'ACTIVE';
      const accountBalance = row[5] !== '0' ? row[5] : '0';
      const accountUnitType = row[6] !== '' ? row[6] : null;
      const accountTransfersOutsideTal = row[7] !== '' ? row[7] : true;
      const accountApprovalSecondArRequired = row[8] !== '' ? row[8] : false;
      const unitBlockStart =
        row[9] === '' || row[9] === null || row[9] === undefined ? '0' : row[9];
      const unitBlockEnd =
        row[10] === '' || row[10] === null || row[10] === undefined
          ? '999'
          : row[10];
      const unitBlockOriginalCp =
        row[11] === '' || row[11] === null || row[11] === undefined
          ? 'CP2'
          : row[11];
      const unitBlockApplicableCp =
        row[12] === '' || row[12] === null || row[12] === undefined
          ? 'CP2'
          : row[12];
      const unitBlockActivity = row[13] !== '' ? row[13] : '';
      const unitBlockUnitType =
        row[14] === '' || row[14] === null || row[14] === undefined
          ? ''
          : row[14];
      const unitBlockAccountIdentifier =
        row[15] === '' || row[15] === null || row[15] === undefined
          ? '1000'
          : row[15];
      const commitmentPeriod = row[16] !== '' ? row[16] : '';
      const accountHolderType =
        row[17] === 'Null' || row[17] === '' || row[17] === undefined
          ? 'INDIVIDUAL'
          : row[17];
      const typeLabel =
        row[18] === 'Null' || row[18] === '' || row[18] === undefined
          ? 'Party Holding Account'
          : row[18];
      const sop = !(
        row[19] === 'Null' ||
        row[19] === '' ||
        row[19] === undefined ||
        row[19] === 'false'
      );
      const reservedForTransaction =
        row[20] === 'Null' || row[20] === '' || row[20] === undefined
          ? ''
          : row[20];
      const is_single_person_approval_required =
        row[21] === 'Null' || row[21] === '' || row[21] === undefined
          ? ''
          : row[21];
      console.log(
        `is_single_person_approval_required: '${is_single_person_approval_required}'.`
      );

      const accountDetails =
        PHA_2.find((o) => o._full_identifier === accountFullIdentifier) !==
        undefined
          ? PHA_2.find((o) => o._full_identifier === accountFullIdentifier)
          : PHA.find((o) => o._full_identifier === accountFullIdentifier);

      /**
       * Creating the account holder
       */
      //
      // if we have more than one accountHolderType GOVERNMENT accounts in one scenario, then these accounts should
      // have the same account holder. As a result, for the 2+ GOVERNMENT account we have to:
      // 1. bypass the "Creating the account holder" functionality (RegistryDbContactBuilder)
      // 2. in "Building the account" (RegistryDbRegistryAccountBuilder) we have to set the already existing government account holder id.
      //
      // create the account holder only if:
      // a. (the account has not accountHolderType GOVERNMENT) or
      // b. (if the account has accountHolderType GOVERNMENT and NO GOVERNMENT has already been created)
      //
      const governmentAccountHolderId =
        await KnowsThePage.getGovernmentAccountHolderId();
      let accountHolder: RegistryDbAccountHolder;

      // case: do not create account holder
      if (
        accountHolderType === 'GOVERNMENT' &&
        !(governmentAccountHolderId === '')
      ) {
        // console.log(`>>>>>> For the new account with name '${accountName}', NO New Account holder will be created.`);
      } else {
        // case: create account holder
        let accountHolderContact = new RegistryDbContactBuilder().build();
        accountHolderContact = (
          await RegistryDbContactTestData.loadContactsInDB([
            accountHolderContact,
          ])
        )[0];
        accountHolder = new RegistryDbAccountHolderBuilder()
          .name('Test Holder Name')
          .contact_id(accountHolderContact.id)
          .type(accountHolderType)
          .build();

        accountHolder = (
          await RegistryDbAccountHolderTestData.loadAccountHolders([
            accountHolder,
          ])
        )[0];

        // in case of FIRST CREATION OF GOVERNMENT account, populate governmentAccountHolderId
        if (accountHolderType === 'GOVERNMENT') {
          await KnowsThePage.setGovernmentAccountHolderId(accountHolder.id);
        }
      }

      /**
       * Creating the account contact details
       */
      let accountContact = new RegistryDbContactBuilder().build();
      accountContact = (
        await RegistryDbContactTestData.loadContactsInDB([accountContact])
      )[0];

      /**
       * Generating the account compliant entity (if any) and the account information (identifier, full identifier,
       * types etc.)
       */
      let createdCompliantEntity: [RegistryDbCompliantEntity, any];
      if (accountHolderType !== 'GOVERNMENT') {
        let accountInformation;
        switch (registryAccountType) {
          case AccountType[AccountType.OPERATOR_HOLDING_ACCOUNT]: {
            const compliantEntity =
              new RegistryDbCompliantEntityBuilder().build();
            const installation = new RegistryDbInstallationBuilder().build();
            createdCompliantEntity = (
              await RegistryDbCompliantEntityTestData.loadCompliantEntities(
                [[compliantEntity, installation]],
                CompliantEntityType.INSTALLATION
              )
            )[0];

            accountInformation = OHA[ohaIndex++];
            break;
          }
          case AccountType[AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT]: {
            const compliantEntity =
              new RegistryDbCompliantEntityBuilder().build();
            const aircraftOperator =
              new RegistryDbAircraftOperatorBuilder().build();
            createdCompliantEntity = (
              await RegistryDbCompliantEntityTestData.loadCompliantEntities(
                [[compliantEntity, aircraftOperator]],
                CompliantEntityType.AIRCRAFT_OPERATOR
              )
            )[0];

            accountInformation = AOHA[aohaIndex++];
            break;
          }
          case AccountType[AccountType.TRADING_ACCOUNT]: {
            accountInformation = TRADING[tradingIndex++];
            break;
          }
          case AccountType[AccountType.PERSON_HOLDING_ACCOUNT]: {
            accountInformation = PHA[personIndex++];
            break;
          }
        }
      }
      /**
       * Building the account
       */

      const accountOpeningDate = moment(new Date()).format(
        'YYYY-MM-DD HH:mm:ss'
      );

      const account = new RegistryDbRegistryAccountBuilder()
        .contact_id(accountContact.id)
        .account_holder_id(
          accountHolderType === 'GOVERNMENT'
            ? KnowsThePage.getGovernmentAccountHolderId()
            : accountHolder.id
        )
        .identifier(accountDetails._identifier)
        .compliant_entity_id(
          registryAccountType === 'OPERATOR_HOLDING_ACCOUNT' ||
            registryAccountType === 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT'
            ? createdCompliantEntity[0].id
            : null
        )
        .commitment_period_code(commitmentPeriod)
        .opening_date(accountOpeningDate)
        .account_status(accountStatus)
        .request_status(requestStatus)
        .registry_account_type(registryAccountType)
        .balance(accountBalance)
        .unit_type(accountUnitType)
        .kyoto_account_type(kyotoAccountType)
        .commitment_period_code(commitmentPeriod)
        .check_digits(accountDetails._check_digits)
        .type_label(typeLabel)
        .account_name(accountName)
        .full_identifier(accountFullIdentifier)
        .single_person_approval_required(
          is_single_person_approval_required === 'false'
            ? convertToBoolean('false')
            : convertToBoolean('true')
        )
        .transfers_outside_tal(convertToBoolean(accountTransfersOutsideTal))
        .approval_second_ar_required(
          convertToBoolean(accountApprovalSecondArRequired)
        )
        .registry_code(accountDetails._registryCode)
        .build();
      await RegistryDbAccountTestData.loadAccounts([account]);

      /**
       * Building the unit block
       */

      const acquisitionDate = moment(new Date()).format(
        'YYYY-MM-DD HH:mm:ss.SSS'
      );

      const unit_block = new RegistryDbUnitBlockBuilder()
        .start_block(unitBlockStart)
        .end_block(unitBlockEnd)
        .unit_type(unitBlockUnitType)
        .original_period(unitBlockOriginalCp)
        .applicable_period(unitBlockApplicableCp)
        .account_identifier(unitBlockAccountIdentifier)
        .environmental_activity(
          unitBlockActivity === '' ? null : unitBlockActivity
        )
        .reserved_for_transaction(
          reservedForTransaction === '' ? null : reservedForTransaction
        )
        .originating_country_code('GB')
        .acquisition_date(acquisitionDate)
        .expiry_date(null)
        .last_modified_date(null)
        .project_number(null)
        .project_track(null)
        .sop(sop)
        .build();
      await RegistryDbUnitBlockTestData.loadUnitBlocksInDB([unit_block]);

      /**
       * If the account is ETS instead of KP, then:
       * 1. add account to TRANSACTION LOG DATABASE
       * 2. add unit block to TRANSACTION LOG DATABASE
       */
      if (accountPrefix === 'UK') {
        console.log(
          `Inserting into 'transaction log' database, 'account' table, regarding account: '${accountFullIdentifier}'.`
        );
        const transactionLogDbAccount = new DbTransactionLogAccountBuilder()
          .account_identifier(accountDetails._identifier)
          .account_account_name(accountName)
          .account_commitment_period_code(commitmentPeriod)
          .account_full_identifier(accountFullIdentifier)
          .account_check_digits(accountDetails._check_digits)
          .account_opening_date(accountOpeningDate)
          .build();
        await TransactionLogDbAccountTestData.loadAccounts([
          transactionLogDbAccount,
        ]);

        console.log(
          `Inserting into 'transaction log' database, 'unit block' table, regarding account: '${accountFullIdentifier}'.`
        );
        const transactionLogDbUnitBlock = new DbTransactionLogUnitBlockBuilder()
          .unit_block_start_block(unitBlockStart)
          .unit_block_end_block(unitBlockEnd)
          .unit_block_unit_type(accountUnitType)
          .unit_block_account_identifier(accountDetails._identifier)
          .unit_block_acquisition_date(acquisitionDate)
          .unit_block_last_modified_date(acquisitionDate)
          .unit_block_year(null)
          .build();
        await TransactionLogDbUnitBlockTestData.loadUnitBlocks([
          transactionLogDbUnitBlock,
        ]);
      }
    }
  }
);
