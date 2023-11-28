import {
  registryDbResetAllIndices,
  registryDbResetTestData,
} from './registry-db/registry-test-data.util';
import {
  transactionLogDbResetAllIndices,
  transactionLogDbResetTestData,
} from './transaction-log-db/transaction-log-test-data.util';
import { delay } from '../step.util';

export async function ResetAllTestData() {
  console.log(`\n\nEntered 'ResetAllTestData' function.`);
    await Promise.all([delay(1000)]).then(result => { console.log(`${result} promise resolved for delay`)});
    await Promise.all([registryDbResetTestData()]).then(result => { console.log(`${result} promise resolved for registryDbResetTestData`)});
    await Promise.all([registryDbResetAllIndices()]).then(result => { console.log(`${result} promise resolved for registryDbResetAllIndices`)});
    await Promise.all([transactionLogDbResetTestData()]).then(result => { console.log(`${result} promise resolved for transactionLogDbResetTestData`)});
    await Promise.all([transactionLogDbResetAllIndices()]).then(result => { console.log(`${result} promise resolved for transactionLogDbResetAllIndices`)});
    await Promise.all([delay(1000)]).then(result => { console.log(`${result} promise resolved for delay`)});
  console.log(`Exiting 'ResetAllTestData' function.\n\n`);
}
