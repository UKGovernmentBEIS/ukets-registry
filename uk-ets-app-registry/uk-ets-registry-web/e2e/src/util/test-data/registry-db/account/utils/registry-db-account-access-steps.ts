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
  TRADING,
} from '../model/data/registry-db-account-properties';
import { RegistryDbAccountTestData } from './registry-db-account.util';
import { RegistryDbCompliantEntity } from '../model/compliant-entity/registry-db-compliant-entity';
import moment from 'moment';
import { AccountType } from '../model/types/registry-db-account-type.enum';
import { CompliantEntityType } from '../model/types/registry-db-compliant-entity.enum';
import { RegistryDbAccountAccessTestData } from './registry-db-account-access.util';
import { RegistryDbAccountAccessBuilder } from '../model/account-access/registry-db-account-access.builder';
import { GlobalState } from '../../../../../step-definitions/ui-step-definitions';
import { RegistryDbAccountHolderRepresentativeBuilder } from '../../user/model/registry-db-account-holder-representative.builder';
import { RegistryDbUserTestData } from '../../user/utils/registry-db-user.util';
import { RegistryDbTrustedRegistryDbAccountTestData } from '../../trusted-account/types/registry-db-trusted-account.util';
import { RegistryDbTrustedAccountBuilder } from '../../trusted-account/model/registry-db-trusted-account.builder';
import { RegistryDbAccountHolderRepTestData } from '../../user/utils/registry-db-account-holder-representative.util';
import { RegistryDbTestUser } from '../../user/model/registry-db-test-user';
import { RegistryDbRegistryAccountBuilder } from '../model/account/registry-db-registry-account.builder';
import * as assert from 'assert';
import { RegistryDbUnitBlockBuilder } from '../../transaction/model/unit-block/registry-db-unit-block.builder';
import { RegistryDbUnitBlockTestData } from '../../transaction/registry-db-unit-block.util';
import { accountUkOrGb } from './registry-db-account-steps';
import { convertToBoolean, getCurrentYear } from '../../../../step.util';
import { DbTransactionLogAccountBuilder } from '../../../transaction-log-db/account/model/db-transaction-log-account.builder';
import { TransactionLogDbAccountTestData } from '../../../transaction-log-db/account/db-transaction-log-account.util';
import { DbTransactionLogUnitBlockBuilder } from '../../../transaction-log-db/unit-block/model/db-transaction-log-unit-block.builder';
import { TransactionLogDbUnitBlockTestData } from '../../../transaction-log-db/unit-block/db-transaction-log-unit-block.util';
import { RegistryDbInstallationOwnershipTestData } from '../../installation-ownership/registry-db-installation-ownership.util';
import { RegistryDbInstallationOwnershipBuilder } from '../../installation-ownership/model/registry-db-installation-ownership.builder';
import { RegistryDbAccountOwnershipBuilder } from '../../account-ownership/model/registry-db-account-ownership.builder';
import { RegistryDbAccountOwnershipTestData } from '../../account-ownership/registry-db-account-ownership.util';
import { RegistryDbEmissionsEntryTestData } from '../../emissions_entry/registry-db-emissions-entry.util';
import { RegistryDbEmissionsEntryBuilder } from '../../emissions_entry/registry-db-emissions-entry.builder';

Given(
  'I am {string} AR with access right {string} in the account with ID {string}',
  async (
    accountUserAccessState:
      | 'ACTIVE'
      | 'SUSPENDED'
      | 'REMOVED'
      | 'REQUESTED'
      | 'REJECTED',
    accessRight: 'INITIATE_AND_APPROVE' | 'APPROVE' | 'INITIATE' | 'READ_ONLY',
    accountId: string
  ) => {
    // This step should not be used from administrator users (e.g. senior admin, junior admin, read only admin)
    // because admins should have ALL access rights (technically means INITIATE_AND_APPROVE access rights) by default.

    await RegistryDbAccountAccessTestData.loadAccountAccesses([
      new RegistryDbAccountAccessBuilder()
        .state(accountUserAccessState)
        .account_id(accountId)
        .user_id(GlobalState.currentUser.id)
        .accessRight(accessRight)
        .build(),
    ]);
  }
);

Given(
  'I am an AR in the following accounts',
  async (dataTable: TableDefinition) => {
    const rows: string[][] = dataTable.raw();
    let ohaIndex = 0;
    let aohaIndex = 0;
    let tradingIndex = 0;
    let personIndex = 0;

    // This step should not be used from administrator users (e.g. senior admin, junior admin, read only admin)
    // because admins should have ALL access rights (technically means INITIATE_AND_APPROVE access rights) by default.

    for (const row of rows) {
      if (row[0] === 'type') {
        continue;
      }
      const accountType = row[0];
      const accountOpeningStatus = row[1];
      const accountStatus = row[2];
      const accountHolderType = row[3];
      const accountHolderName = row[4];
      const commitmentPeriod =
        row[5] === null || row[5] === undefined || row[5] === '' ? '2' : row[5];

      /**
       * Creating the account holder
       */
      let accountHolderContact = new RegistryDbContactBuilder().build();
      accountHolderContact = (
        await RegistryDbContactTestData.loadContactsInDB([accountHolderContact])
      )[0];
      let accountHolder: RegistryDbAccountHolder;
      accountHolder = new RegistryDbAccountHolderBuilder()
        .contact_id(accountHolderContact.id)
        .type(accountHolderType)
        .name(accountHolderName)
        .build();
      accountHolder = (
        await RegistryDbAccountHolderTestData.loadAccountHolders([
          accountHolder,
        ])
      )[0];

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
      let accountInformation;
      switch (accountType) {
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

      /**
       * Building the account
       */
      const account = new RegistryDbRegistryAccountBuilder()
        .contact_id(accountContact.id)
        .account_holder_id(accountHolder.id)
        .compliant_entity_id(createdCompliantEntity[0].id)
        .identifier(accountInformation._identifier)
        .opening_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
        .commitment_period_code(
          commitmentPeriod === '' ? '2' : commitmentPeriod
        )
        .account_status(accountOpeningStatus)
        .request_status(accountStatus)
        .registry_account_type(accountInformation._registry_account_type)
        .kyoto_account_type(accountInformation._kyoto_account_type)
        .check_digits(accountInformation._check_digits)
        .type_label(accountInformation._type_label)
        .single_person_approval_required(convertToBoolean('true'))
        .full_identifier(accountInformation._full_identifier)
        .build();
      await RegistryDbAccountTestData.loadAccounts([account]);

      /**
       * Generating the access rights
       */
      await RegistryDbAccountAccessTestData.loadAccountAccesses([
        new RegistryDbAccountAccessBuilder()
          .account_id(account.id)
          .user_id(GlobalState.currentUser.id)
          .build(),
      ]);
    }
  }
);

Given(
  'I have created an account with the following properties',
  async (dataTable: TableDefinition) => {
    console.log(
      'Executing step: I have created an account with the following properties:'
    );
    const rows: string[][] = dataTable.raw();
    let accountType: string;
    let accountHolderType: string;
    let accountHolderName: string;
    let compliantEntityEndYear = '2021';
    let accountStatus: string;
    let commitmentPeriod: string;
    let emissions = '';
    let transfersOutsideTal = true;
    let approveSecondAr = false;
    let installationOwnership = '';
    let accountOwnership = '';
    const accountHolderReps: string[] = [];
    const authorisedReps: string[] = [];
    const trustedAccounts: string[] = [];
    let unitsInformation: string[] = [];

    // accountIndex defined at gherkin. If not defined at gherkin level, value 0 used.
    // The value corresponds to registry-db-account-properties.ts account index and it is used when there
    // is a need to create more than 1 account.
    let accountIndex = 0;

    for (const row of rows) {
      console.log(`| ${row[0]} | ${row[1]} |`);

      if (row[0] === 'accountType') {
        accountType = row[1];
      } else if (row[0] === 'holderType') {
        accountHolderType = row[1];
      } else if (row[0] === 'holderName') {
        accountHolderName = row[1];
      } else if (row[0] === 'accountIndex') {
        accountIndex = Number(row[1]);
      } else if (row[0] === 'accountStatus') {
        accountStatus = row[1];
      } else if (row[0] === 'accountHolderRepresentative') {
        accountHolderReps.push(row[1]);
      } else if (row[0] === 'authorisedRepresentative') {
        authorisedReps.push(row[1]);
      } else if (row[0] === 'trustedAccount') {
        trustedAccounts.push(row[1]);
      } else if (row[0] === 'unitsInformation') {
        unitsInformation = row[1].split(`,`);
      } else if (row[0] === 'commitmentPeriod') {
        commitmentPeriod = row[1];
      } else if (row[0] === 'transfersOutsideTal') {
        transfersOutsideTal = convertToBoolean(row[1]);
      } else if (row[0] === 'approveSecondAr') {
        approveSecondAr = convertToBoolean(row[1]);
      } else if (row[0] === 'installationOwnership') {
        // only oha accounts WITH INSTALLATION TRANSFER need installationOwnership
        installationOwnership = row[1];
      } else if (row[0] === 'accountOwnership') {
        // only oha accounts WITH ACCOUNT TRANSFER need accountOwnership
        accountOwnership = row[1];
      } else if (row[0] === 'compliantEntityEndYear') {
        compliantEntityEndYear = row[1];
        if (compliantEntityEndYear === 'not specified') {
          compliantEntityEndYear = null;
        }
      } else if (row[0] === 'emissions') {
        emissions = row[1];
      }
    }

    // default values
    let shouldBuildUnitBlock = false;
    let unitBlockBalance = '';
    let unitBlockType = '';
    let unitBlockStart = '';
    let unitBlockEnd = '';
    let unitBlockAccountIdentifier = '';
    let unitBlockOriginalPeriod = '';
    let unitBlockApplicablePeriod = '';
    let unitBlockActivity = '';
    let unitBlockReservedForTransaction = '';
    let unitBlockSop = false;

    if (unitsInformation.length === 10) {
      shouldBuildUnitBlock = true;
      unitBlockBalance = unitsInformation[0];
      unitBlockType = unitsInformation[1];
      unitBlockStart = unitsInformation[2];
      unitBlockEnd = unitsInformation[3];
      unitBlockAccountIdentifier = unitsInformation[4];
      unitBlockOriginalPeriod = unitsInformation[5];
      unitBlockApplicablePeriod = unitsInformation[6];
      unitBlockActivity = unitsInformation[7];
      unitBlockReservedForTransaction = unitsInformation[8];
      unitBlockSop = convertToBoolean(unitsInformation[9]);
    } else if (unitsInformation.length === 0) {
      console.log(`default unit block and balance used.`);
    } else {
      assert.fail(
        `Invalid arguments ${unitsInformation.length}' into unitsInformation row`
      );
    }

    // commitment period
    if (
      commitmentPeriod === '' ||
      commitmentPeriod === undefined ||
      commitmentPeriod === null
    ) {
      commitmentPeriod = '2';
    }

    /**
     * Creating the account holder
     */
    let accountHolderContact = new RegistryDbContactBuilder().build();
    accountHolderContact = (
      await RegistryDbContactTestData.loadContactsInDB([accountHolderContact])
    )[0];
    let accountHolder: RegistryDbAccountHolder;
    accountHolder = new RegistryDbAccountHolderBuilder()
      .contact_id(accountHolderContact.id)
      .type(accountHolderType)
      .name(accountHolderName)
      .build();

    accountHolder = (
      await RegistryDbAccountHolderTestData.loadAccountHolders([accountHolder])
    )[0];

    /**
     * Creating the account holder representatives
     */
    for (const accountHolderRepName of accountHolderReps) {
      let accountHolderRepContact = new RegistryDbContactBuilder().build();
      accountHolderRepContact = (
        await RegistryDbContactTestData.loadContactsInDB([
          accountHolderRepContact,
        ])
      )[0];
      let accountHolderRep = new RegistryDbAccountHolderRepresentativeBuilder()
        .first_name(accountHolderRepName.split(' ')[0])
        .last_name(accountHolderRepName.split(' ')[1])
        .account_contact_type(accountHolderRepName.split(' ')[2])
        .account_holder_id(accountHolder.id)
        .contact_id(accountHolderRepContact.id)
        .build();
      accountHolderRep = (
        await RegistryDbAccountHolderRepTestData.loadAccountHolderReps([
          accountHolderRep,
        ])
      )[0];
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
    let accountInformation;
    switch (accountType) {
      case AccountType[AccountType.OPERATOR_HOLDING_ACCOUNT]: {
        const compliantEntity = new RegistryDbCompliantEntityBuilder()
          .end_year(compliantEntityEndYear)
          .build();
        const installation = new RegistryDbInstallationBuilder().build();
        createdCompliantEntity = (
          await RegistryDbCompliantEntityTestData.loadCompliantEntities(
            [[compliantEntity, installation]],
            CompliantEntityType.INSTALLATION
          )
        )[0];

        accountInformation = OHA[accountIndex];
        break;
      }
      case AccountType[AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT]: {
        const compliantEntity = new RegistryDbCompliantEntityBuilder()
          .end_year(compliantEntityEndYear)
          .build();
        const aircraftOperator =
          new RegistryDbAircraftOperatorBuilder().build();
        createdCompliantEntity = (
          await RegistryDbCompliantEntityTestData.loadCompliantEntities(
            [[compliantEntity, aircraftOperator]],
            CompliantEntityType.AIRCRAFT_OPERATOR
          )
        )[0];

        accountInformation = AOHA[accountIndex];
        break;
      }
      case AccountType[AccountType.TRADING_ACCOUNT]: {
        accountInformation = TRADING[accountIndex];
        break;
      }
      case AccountType[AccountType.PERSON_HOLDING_ACCOUNT]: {
        accountInformation = PHA[accountIndex];
        break;
      }
    }

    /**
     * Building the account
     */
    const accountOpeningDate = moment(new Date()).format('YYYY-MM-DD HH:mm:ss');
    const account = new RegistryDbRegistryAccountBuilder()
      .contact_id(accountContact.id)
      .account_status(accountStatus)
      .account_holder_id(accountHolder.id)
      .transfers_outside_tal(transfersOutsideTal)
      .approval_second_ar_required(approveSecondAr)
      .registry_code('UK')
      .balance(unitsInformation.length === 0 ? '0' : unitBlockBalance)
      .unit_type(unitsInformation.length === 0 ? null : unitBlockType)
      .commitment_period_code(commitmentPeriod === '' ? '2' : commitmentPeriod)
      .compliant_entity_id(createdCompliantEntity[0].id)
      .identifier(accountInformation._identifier)
      .opening_date(accountOpeningDate)
      .registry_account_type(accountInformation._registry_account_type)
      .kyoto_account_type(accountInformation._kyoto_account_type)
      .check_digits(accountInformation._check_digits)
      .type_label(accountInformation._type_label)
      .single_person_approval_required(convertToBoolean('true'))
      .full_identifier(accountInformation._full_identifier)
      .build();
    await RegistryDbAccountTestData.loadAccounts([account]);

    /**
     * Build installation ownership (if any)
     */
    if (installationOwnership !== '') {
      const bddInstallationId = installationOwnership.split(',')[0];
      const bddPermitIdentifier = installationOwnership.split(',')[1];
      const installation_ownership =
        new RegistryDbInstallationOwnershipBuilder()
          .setAccountId(account.id)
          .setInstallation_id(bddInstallationId)
          .setOwnership_date(
            moment(new Date()).format('YYYY-MM-DD HH:mm:ss.SSS')
          )
          .setPermit_identifier(bddPermitIdentifier)
          .setStatus('ACTIVE')
          .build();
      await RegistryDbInstallationOwnershipTestData.loadInstallationOwnershipInDb(
        [installation_ownership]
      );
    }

    /**
     * Build account ownership (if any)
     */
    if (accountOwnership !== '') {
      const account_ownership = new RegistryDbAccountOwnershipBuilder()
        .setAccountId(account.id)
        .setAccountHolder_id(accountHolder.id)
        .setStatus(accountOwnership)
        .setDateOfOwnership(
          moment(new Date()).format('YYYY-MM-DD HH:mm:ss.SSS')
        )
        .build();
      await RegistryDbAccountOwnershipTestData.loadAccountOwnershipInDb([
        account_ownership,
      ]);
    }

    /**
     * Generating the authorised representatives for the account
     */
    for (const authorisedRepName of authorisedReps) {
      const testUser = new RegistryDbTestUser();
      testUser.username = `authorised_rep_${authorisedRepName
        .toLowerCase()
        .replace(' ', '_')}`;
      testUser.firstName = authorisedRepName.split(' ')[0];
      testUser.lastName = authorisedRepName.split(' ')[1];
      testUser.state = 'ENROLLED';
      testUser.email = `${authorisedRepName
        .toLowerCase()
        .replace(' ', '_')}@test.com`;
      const user = await RegistryDbUserTestData.createUser(testUser, 0);

      await RegistryDbAccountAccessTestData.loadAccountAccesses([
        new RegistryDbAccountAccessBuilder()
          .state(
            authorisedRepName.split(' ')[2] === null
              ? 'ACTIVE'
              : authorisedRepName.split(' ')[2]
          )
          .account_id(account.id)
          .user_id(user.id)
          .build(),
      ]);
    }

    /**
     * Build emissions entry (if any)
     */
    if (emissions !== '') {
      const emissions_entry = new RegistryDbEmissionsEntryBuilder()
        .filename('file_name_placeholder')
        .compliant_entity_id(createdCompliantEntity[0].identifier)
        .year(emissions.split(':')[0])
        .emissions(emissions.split(':')[1])
        .upload_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss.SSS'))
        .build();
      await RegistryDbEmissionsEntryTestData.loadEmissionsEntriesInDB([
        emissions_entry,
      ]);
    }

    /**
     * Build unit block (if any)
     */
    if (shouldBuildUnitBlock) {
      const unit_block = new RegistryDbUnitBlockBuilder()
        .start_block(unitBlockStart)
        .end_block(unitBlockEnd)
        .unit_type(unitBlockType)
        .original_period(unitBlockOriginalPeriod)
        .applicable_period(unitBlockApplicablePeriod)
        .account_identifier(unitBlockAccountIdentifier)
        .environmental_activity(
          unitBlockActivity === 'empty' ? null : unitBlockActivity
        )
        .reserved_for_transaction(
          unitBlockReservedForTransaction === 'empty'
            ? null
            : unitBlockReservedForTransaction
        )
        .originating_country_code('GB')
        .acquisition_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss.SSS'))
        .expiry_date(null)
        .last_modified_date(null)
        .project_number(null)
        .project_track(null)
        .sop(unitBlockSop)
        .build();
      await RegistryDbUnitBlockTestData.loadUnitBlocksInDB([unit_block]);
    }

    /**
     * Generating trusted accounts for the account
     */
    for (const trustedAccount of trustedAccounts) {
      await RegistryDbTrustedRegistryDbAccountTestData.loadTrustedAccountsInDB([
        new RegistryDbTrustedAccountBuilder()
          .account_id(account.id)
          .activation_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
          .trusted_account_full_identifier(trustedAccount.split(' ')[0])
          .status(trustedAccount.split(' ')[1])
          .description('')
          .build(),
      ]);
    }

    /**
     * If the account is ETS instead of KP, then:
     * 1. add account to TRANSACTION LOG DATABASE
     * 2. add unit block to TRANSACTION LOG DATABASE
     */
    const accountFullIdentifier = accountInformation._full_identifier;
    const accountPrefix = accountUkOrGb(accountFullIdentifier);
    if (accountPrefix === 'UK') {
      const accountName = `${accountInformation._type_label} ${String(
        accountInformation._identifier
      )}`;
      const transactionLogDbAccount = new DbTransactionLogAccountBuilder()
        .account_identifier(accountInformation._identifier)
        .account_account_name(accountName)
        .account_commitment_period_code(commitmentPeriod)
        .account_full_identifier(accountFullIdentifier)
        .account_check_digits(accountInformation._check_digits)
        .account_opening_date(accountOpeningDate)
        .build();
      await TransactionLogDbAccountTestData.loadAccounts([
        transactionLogDbAccount,
      ]);
      if (shouldBuildUnitBlock) {
        const acquisitionDate = moment(new Date()).format(
          'YYYY-MM-DD HH:mm:ss.SSS'
        );
        const transactionLogDbUnitBlock = new DbTransactionLogUnitBlockBuilder()
          .unit_block_start_block(unitBlockStart)
          .unit_block_end_block(unitBlockEnd)
          .unit_block_unit_type(unitBlockType)
          .unit_block_account_identifier(accountInformation._identifier)
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
