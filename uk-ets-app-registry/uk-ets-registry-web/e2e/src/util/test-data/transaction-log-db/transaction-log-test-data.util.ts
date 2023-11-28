import { TransactionLogDbUnitBlockTestData } from './unit-block/db-transaction-log-unit-block.util';
import { TransactionLogDbTransactionResponseTestData } from './transaction-response/db-transaction-log-transaction-response.util';
import { TransactionLogDbTransactionHistoryTestData } from './transaction-history/db-transaction-log-transaction-history.util';
import { TransactionLogDbTransactionBlockTestData } from './transaction-block/db-transaction-log-transaction-block.util';
import { TransactionLogDbTransactionTestData } from './transaction/db-transaction-log-transaction-block.util';
import { TransactionLogDbReconciliationHistoryTestData } from './reconciliation-history/db-transaction-log-reconciliation-history.util';
import { TransactionLogDbReconciliationFailedEntryTestData } from './reconciliation-failed-entry/db-transaction-log-reconciliation-failed-entry.util';
import { TransactionLogDbReconciliationTestData } from './reconciliation/db-transaction-log-reconciliation.util';
import { TransactionLogDbAccountTestData } from './account/db-transaction-log-account.util';
import { TransactionLogDbAllocationPhaseTestData } from './allocation-phase/db-transaction-log-allocation_phase.util';
import { TransactionLogDbDatabaseChangeLogTestData } from './database-change-log/db-transaction-log-database-change-log.util';
import { TransactionLogDbDatabaseChangeLogLockTestData } from './database-change-log-lock/db-transaction-log-database-change-log-lock.util';

export async function transactionLogDbResetTestData() {
  console.log(`Entered 'transactionLogDbResetTestData'.`);
  await Promise.all([TransactionLogDbUnitBlockTestData.deleteAllUnitBlocksFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbUnitBlockTestData.deleteAllUnitBlocksFromDB`)});
  await Promise.all([TransactionLogDbTransactionResponseTestData.deleteAllTransactionResponsesFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbTransactionResponseTestData.deleteAllTransactionResponsesFromDB`)});
  await Promise.all([TransactionLogDbTransactionHistoryTestData.deleteAllTransactionHistoryFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbTransactionHistoryTestData.deleteAllTransactionHistoryFromDB`)});
  await Promise.all([TransactionLogDbTransactionBlockTestData.deleteAllTransactionBlockFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbTransactionBlockTestData.deleteAllTransactionBlockFromDB`)});
  await Promise.all([TransactionLogDbTransactionTestData.deleteAllTransactionFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbTransactionTestData.deleteAllTransactionFromDB`)});
  await Promise.all([TransactionLogDbReconciliationHistoryTestData.deleteAllReconciliationHistoryFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbReconciliationHistoryTestData.deleteAllReconciliationHistoryFromDB`)});
  await Promise.all([TransactionLogDbReconciliationFailedEntryTestData.deleteAllReconciliationFailedEntryFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbReconciliationFailedEntryTestData.deleteAllReconciliationFailedEntryFromDB`)});
  await Promise.all([TransactionLogDbReconciliationTestData.deleteAllReconciliationsFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbReconciliationTestData.deleteAllReconciliationsFromDB`)});
  await Promise.all([TransactionLogDbAccountTestData.deleteAllAccountFromDB()]).then(result => { console.log(`${result} promise resolved for TransactionLogDbAccountTestData.deleteAllAccountFromDB`)});
  console.log(`Exiting 'transactionLogDbResetTestData'.`);
}

export async function transactionLogDbResetAllIndices() {
  console.log(`Entered 'transactionLogDbResetAllIndices'.`);
  await Promise.all([TransactionLogDbUnitBlockTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbUnitBlockTestData`)});
  await Promise.all([TransactionLogDbTransactionResponseTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbTransactionResponseTestData`)});
  await Promise.all([TransactionLogDbTransactionHistoryTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbTransactionHistoryTestData`)});
  await Promise.all([TransactionLogDbTransactionBlockTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbTransactionBlockTestData`)});
  await Promise.all([TransactionLogDbTransactionTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbTransactionTestData`)});
  await Promise.all([TransactionLogDbReconciliationHistoryTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbReconciliationHistoryTestData`)});
  await Promise.all([TransactionLogDbReconciliationFailedEntryTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbReconciliationFailedEntryTestData`)});
  await Promise.all([TransactionLogDbReconciliationTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbReconciliationTestData`)});
  await Promise.all([TransactionLogDbAccountTestData.resetIndices()]).then(result => { console.log(`${result} index promise resolved for TransactionLogDbAccountTestData`)});
  console.log(`Exiting 'transactionLogDbResetAllIndices'.`);
}

