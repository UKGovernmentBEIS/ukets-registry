import { Given, TableDefinition } from 'cucumber';
import { RegistryDbTransactionTestData } from './registry-db-transaction.util';
import { RegistryDbTransactionBlockTestData } from './registry-db-transaction-block.util';
import moment from 'moment';
import { RegistryDbTransactionBuilder } from './model/transaction/registry-db-transaction.builder';
import { RegistryDbTransactionBlockBuilder } from './model/transaction-block/registry-db-transaction-block.builder';
import { RegistryDbTaskBuilder } from '../task/model/registry-db-task.builder';
import { RegistryDbTaskTestData } from '../task/utils/registry-db-task.util';
import { PHA_2 } from '../account/model/data/registry-db-account-properties';
import { getTransactionType } from './model/types/registry-db-transaction-type.enum';
import { getTransactionStatus } from './model/types/registry-db-transaction-status.enum';
import { getUnitType } from '../common/registry-db-unit-type.enum';
import { getCommitmentPeriod } from '../common/registry-db-commitment-period.enum';
import { getEnvironmentalActivity } from '../registry-level/model/types/registry-db-envirnomental-activity.enum';

Given(
  'I create the following transactions, transaction tasks, levels and units',
  async (dataTable: TableDefinition) => {
    console.log(
      `Executing step: I create the following transactions, transaction tasks, levels and units`
    );

    const rows: string[][] = dataTable.raw();

    let transactionType = '';
    let transactionStatus = '';
    let commitmentPeriod = '';
    let acquiringAccount = '';
    let transferringAccount = '';
    let unitType = '';
    let quantity = '';
    let accountId = '';
    const startingBlock = 10000003000;
    let endingBlock: number;
    let claimedBy = '';
    let taskStatus = '';
    let initiatedBy = '';
    let environmentalActivity = '';
    let originatingCountryCode = '';

    for (const row of rows) {
      console.log(`| ${row[0]} | ${row[1]} |`);

      if (row[0] === 'fieldName') {
        continue;
      }
      switch (row[0]) {
        case 'Transaction type': {
          transactionType = row[1];
          break;
        }
        case 'Transaction status': {
          transactionStatus = row[1];
          break;
        }
        case 'Environmental activity': {
          environmentalActivity = row[1];
          break;
        }
        case 'Commitment period': {
          commitmentPeriod = row[1];
          break;
        }
        case 'Acquiring account': {
          acquiringAccount = row[1];
          break;
        }
        case 'Transferring account': {
          transferringAccount = row[1];
          break;
        }

        // uk transactions => kp => originatingCountryCode UK
        // ets transactions => ets => originatingCountryCode GB
        case 'Originating Country Code': {
          originatingCountryCode = row[1];
          break;
        }

        case 'Unit type': {
          unitType = row[1];
          break;
        }
        case 'Quantity to issue': {
          quantity = row[1];
          endingBlock = startingBlock + +quantity - 1;
          break;
        }
        case 'Account ID': {
          accountId = row[1];
          break;
        }
        case 'Claimed by': {
          claimedBy = row[1];
          break;
        }
        case 'Initiated by': {
          initiatedBy = row[1];
          break;
        }
        case 'Task status': {
          taskStatus = row[1];
          break;
        }
      }
    }

    console.log(
      `transaction originatingCountryCode is set to: '${originatingCountryCode}'.`
    );

    const acquiringAccountDetails = PHA_2.find(
      (o) => o._full_identifier === acquiringAccount
    );
    console.log(
      `acquiringAccountDetails._full_identifier: '${acquiringAccountDetails._full_identifier}'.`
    );
    console.log(
      `acquiringAccountDetails._registryCode: '${acquiringAccountDetails._registryCode}'.`
    );

    const transferringAccountDetails = PHA_2.find(
      (o) => o._full_identifier === transferringAccount
    );
    console.log(
      `transferringAccountDetails._full_identifier: '${transferringAccountDetails._full_identifier}'.`
    );
    console.log(
      `transferringAccountDetails._registryCode: '${transferringAccountDetails._registryCode}'.`
    );

    const transactionIdentifier =
      acquiringAccountDetails._registryCode + accountId;
    console.log(`transactionIdentifier: '${transactionIdentifier}'.`);

    /**
     * Build Transaction
     */
    const transaction = new RegistryDbTransactionBuilder()
      .identifier(transactionIdentifier)
      .type(getTransactionType(transactionType))
      .status(
        transactionStatus === '' || transactionStatus === null
          ? getTransactionStatus('Awaiting approval')
          : getTransactionStatus(transactionStatus)
      )
      .quantity(quantity)
      .acquiring_account_identifier(acquiringAccountDetails._identifier)
      .acquiring_account_type(acquiringAccountDetails._kyoto_account_type)
      .acquiring_account_registry_code(acquiringAccountDetails._registryCode)
      .acquiring_account_full_identifier(
        acquiringAccountDetails._full_identifier
      )
      .transferring_account_identifier(transferringAccountDetails._identifier)
      .transferring_account_type(transferringAccountDetails._kyoto_account_type)
      .transferring_account_registry_code(
        transferringAccountDetails._registryCode
      )
      .transferring_account_full_identifier(
        transferringAccountDetails._full_identifier
      )
      .started(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
      .last_updated(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
      .unit_type(getUnitType(unitType))
      .execution_date(null)
      .notification_identifier(null)
      .build();
    await RegistryDbTransactionTestData.loadTransactionsInDB([transaction]);

    /**
     * Build Transaction Block
     */
    const transactionBlock = new RegistryDbTransactionBlockBuilder()
      .start_block(startingBlock.toString())
      .end_block(endingBlock.toString())
      .unit_type(getUnitType(unitType))
      .originating_country_code(originatingCountryCode)
      .original_period(getCommitmentPeriod(commitmentPeriod))
      .applicable_period(getCommitmentPeriod(commitmentPeriod))
      .environmental_activity(getEnvironmentalActivity(environmentalActivity))
      .expiry_date(null)
      .transaction_id(transaction.id)
      .project_number(null)
      .project_track(null)
      .sop(false)
      .build();
    await RegistryDbTransactionBlockTestData.loadTransactionBlocksInDB([
      transactionBlock,
    ]);

    /**
     * Build Task
     */
    const task = new RegistryDbTaskBuilder()
      .account_id(accountId)
      .transaction_identifier(transaction.identifier)
      .claimed_by(claimedBy === '' ? null : claimedBy)
      .claimed_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
      .status(taskStatus === '' ? 'SUBMITTED_NOT_YET_APPROVED' : taskStatus)
      .outcome(null)
      .type('TRANSACTION_REQUEST')
      .request_identifier(accountId)
      .initiated_by(initiatedBy === '' ? null : initiatedBy)
      .initiated_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
      .completed_by(taskStatus === '' ? null : claimedBy)
      .completed_date(
        taskStatus === ''
          ? null
          : moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
      )
      .parent_task_id(null)
      .user_id(claimedBy === '' ? null : claimedBy)
      .recipient_account_number(transaction.acquiring_account_identifier)
      .build();
    await RegistryDbTaskTestData.loadTasks([task]);
  }
);

Given(
  'I create the following transaction tasks, levels and units',
  async (dataTable: TableDefinition) => {
    const rows: string[][] = dataTable.raw();

    let acquiringAccount = '';
    let accountId = '';
    let claimedBy = '';
    let initiatedBy = '';

    for (const row of rows) {
      if (row[0] === 'fieldName') {
        continue;
      }
      switch (row[0]) {
        case 'Acquiring account': {
          acquiringAccount = row[1];
          break;
        }
        case 'Account ID': {
          accountId = row[1];
          break;
        }
        case 'Claimed by': {
          claimedBy = row[1];
          break;
        }
        case 'Initiated by': {
          initiatedBy = row[1];
          break;
        }
      }
    }

    const acquiringAccountDetails = PHA_2.find(
      (o) => o._full_identifier === acquiringAccount
    );

    /**
     * Build Task
     */
    const task = new RegistryDbTaskBuilder()
      .account_id(accountId)
      .transaction_identifier(null)
      .claimed_by(claimedBy === '' ? null : claimedBy)
      .claimed_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
      .status('SUBMITTED_NOT_YET_APPROVED')
      .outcome(null)
      .type('TRANSACTION_REQUEST')
      .request_identifier(accountId)
      .initiated_by(initiatedBy === '' ? null : initiatedBy)
      .initiated_date(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
      .completed_by(null)
      .completed_date(null)
      .parent_task_id(null)
      .user_id(claimedBy === '' ? null : claimedBy)
      .recipient_account_number(acquiringAccountDetails._identifier)
      .build();
    await RegistryDbTaskTestData.loadTasks([task]);
  }
);
