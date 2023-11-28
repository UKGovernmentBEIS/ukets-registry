import { RegistryDbContactTestData } from './contact/utils/registry-db-contact.util';
import { RegistryDbRegistryLevelTestData } from './registry-level/utils/registry-db-registry-level.util';
import { RegistryDbTransactionBlockTestData } from './transaction/registry-db-transaction-block.util';
import { RegistryDbTransactionTestData } from './transaction/registry-db-transaction.util';
import { RegistryDbUnitBlockTestData } from './transaction/registry-db-unit-block.util';
import { RegistryDbTrustedRegistryDbAccountTestData } from './trusted-account/types/registry-db-trusted-account.util';
import { RegistryDbAccountAccessTestData } from './account/utils/registry-db-account-access.util';
import { RegistryDbAllocationEntryTestData } from './allocation-entry/registry-db-allocation-entry.util';
import { RegistryDbAccountHolderRepTestData } from './user/utils/registry-db-account-holder-representative.util';
import { RegistryDbCompliantEntityTestData } from './account/utils/registry-db-compliant-entity.util';
import { RegistryDbUserFilesTestData } from './user-files/utils/registry-db-user-files.util';
import { RegistryDbTransactionResponseTestData } from './transaction-response/registry-db-transaction-response.util';
import { RegistryDbReconciliationFailedEntryTestData } from './reconciliation-failed-entry/registry-db-reconciliation-failed-entry.util';
import { RegistryDbReconciliationHistoryTestData } from './reconciliation-history/registry-db-reconciliation-history.util';
import { RegistryDbReconciliationTestData } from './reconciliation/registry-db-reconciliation.util';
import { RegistryDbDomainEventTestData } from './domain-event/registry-db-domain-event.util';
import { RegistryDbAcceptMessageLogTestData } from './accept-message-log/registry-db-accept-message-log.util';
import { RegistryDbItlNotificationBlockTestData } from './itl-notification-block/registry-db-itl-notification-block.util';
import { RegistryDbItlNotificationTestData } from './itl-notification/registry-db-itl-notification.util';
import { RegistryDbItlNotificationHistoryTestData } from './itl-notification-history/registry-db-itl-notification-history.util';
import { RegistryDbEtsTransactionsTestData } from './ets-transactions/utils/registry-db-ets-transactions.util';
import { RegistryDbTransactionMessageTestData } from './transaction-message/registry-db-transaction-message.util';
import { RegistryDbTransactionHistoryTestData } from './transaction-history/registry-db-transaction-history.util';
import { RegistryDbUserTestData } from './user/utils/registry-db-user.util';
import { RegistryDbAllocationStatusTestData } from './allocation-status/registry-db-allocation-status.util';
import { RegistryDbAccountHolderTestData } from './account/utils/registry-db-account-holder.util';
import { RegistryDbAccountTestData } from './account/utils/registry-db-account.util';
import { RegistryDbTaskTestData } from './task/utils/registry-db-task.util';
import { RegistryDbAllocationPhaseTestData } from './allocation-phase/utils/registry-db-allocation-phase.util';
import { RegistryDbInstallationOwnershipTestData } from './installation-ownership/registry-db-installation-ownership.util';
import { RegistryDbAccountOwnershipTestData } from './account-ownership/registry-db-account-ownership.util';
import { RegistryDbEmissionsEntryTestData } from './emissions_entry/registry-db-emissions-entry.util';
import { RegistryDbExcludeEmissionsEntryTestData } from './exclude_emissions_entry/registry-db-exclude-emissions-entry.util';
import { RegistryDbTaskSearchMetadataTestData } from './task-search-metadata/registry-db-task-search-metadata.util';
import { RegistryDbTransactionConnectionTestData } from './transaction-connection/registry-db-transaction-connection.util';

export async function registryDbResetTestData() {
  console.log(`Entered 'registryDbResetTestData' function.`);
  await Promise.all([
    RegistryDbInstallationOwnershipTestData.deleteAllInstallationOwnershipFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbInstallationOwnershipTestData.deleteAllInstallationOwnershipFromDB`
    );
  });
  await Promise.all([
    RegistryDbAccountOwnershipTestData.deleteAllAccountOwnershipFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbAccountOwnershipTestData.deleteAllAccountOwnershipFromDB`
    );
  });
  await Promise.all([
    RegistryDbTrustedRegistryDbAccountTestData.deleteAllTrustedAccountsFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTrustedRegistryDbAccountTestData.deleteAllTrustedAccountsFromDB`
    );
  });
  await Promise.all([
    RegistryDbTaskSearchMetadataTestData.deleteAllTaskSearchMetadataFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTaskSearchMetadataTestData.deleteAllTaskSearchMetadataFromDB`
    );
  });
  await Promise.all([RegistryDbTaskTestData.deleteAllTasksFromDB()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbTaskTestData.deleteAllTasksFromDB`
      );
    }
  );
  await Promise.all([
    RegistryDbAccountAccessTestData.deleteAllAccountAccessesFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbAccountAccessTestData.deleteAllAccountAccessesFromDB`
    );
  });
  await Promise.all([RegistryDbAccountTestData.deleteAllAccountsFromDB()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAccountTestData.deleteAllAccountsFromDB`
      );
    }
  );
  await Promise.all([
    RegistryDbAccountHolderRepTestData.deleteAllAccountHolderRepsFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbAccountHolderRepTestData.deleteAllAccountHolderRepsFromDB`
    );
  });
  await Promise.all([
    RegistryDbAccountHolderTestData.deleteAllAccountHoldersFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbAccountHolderTestData.deleteAllAccountHoldersFromDB`
    );
  });
  await Promise.all([
    RegistryDbAllocationEntryTestData.deleteAllocationEntryInDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbAllocationEntryTestData.deleteAllocationEntryInDB`
    );
  });
  await Promise.all([
    RegistryDbAllocationStatusTestData.deleteAllocationStatusInDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbAllocationStatusTestData.deleteAllocationStatusInDB`
    );
  });
  await Promise.all([
    RegistryDbCompliantEntityTestData.deleteAllCompliantEntitiesFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbCompliantEntityTestData.deleteAllCompliantEntitiesFromDB`
    );
  });
  await Promise.all([
    RegistryDbRegistryLevelTestData.deleteAllRegistryLevelsFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbRegistryLevelTestData.deleteAllRegistryLevelsFromDB`
    );
  });
  await Promise.all([
    RegistryDbUserFilesTestData.deleteAllUserFilesFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbUserFilesTestData.deleteAllUserFilesFromDB`
    );
  });
  //
  // Note 1:
  // Currently e2e/local env keycloak contains by default the users with usernames:
  // registration-api-user, system-admin, uk-ets-admin.
  //
  // These users are not used by test automation. In case of these users usage
  // (e.g. for manual testing in the aforementioned envs), registry db tables "users" and
  // "user role mapping" have to contain the corresponding data related.
  // Automated tests create their own test data.
  //
  // Note 2: Users "system-admin" and "uk-ets-admin" can be used without 2fa authentication.
  //
  await Promise.all([
    RegistryDbUserTestData.deleteAllUsersFromKeycloakAndDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbUserTestData.deleteAllUsersFromKeycloakAndDB`
    );
  });
  await Promise.all([
    RegistryDbTransactionConnectionTestData.deleteAllTransactionConnectionFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionConnectionTestData.deleteAllTransactionConnectionFromDB`
    );
  });
  await Promise.all([
    RegistryDbTransactionResponseTestData.deleteAllTransactionResponseFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionResponseTestData.deleteAllTransactionResponseFromDB`
    );
  });
  await Promise.all([
    RegistryDbTransactionBlockTestData.deleteAllTransactionBlocksFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionBlockTestData.deleteAllTransactionBlocksFromDB`
    );
  });
  await Promise.all([
    RegistryDbTransactionHistoryTestData.deleteAllTransactionHistoryFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionHistoryTestData.deleteAllTransactionHistoryFromDB`
    );
  });
  await Promise.all([
    RegistryDbTransactionMessageTestData.deleteAllTransactionsMessageFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionMessageTestData.deleteAllTransactionsMessageFromDB`
    );
  });
  await Promise.all([
    RegistryDbTransactionTestData.deleteAllTransactionsFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionTestData.deleteAllTransactionsFromDB`
    );
  });
  await Promise.all([
    RegistryDbUnitBlockTestData.deleteAllUnitBlocksFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbUnitBlockTestData.deleteAllUnitBlocksFromDB`
    );
  });
  await Promise.all([RegistryDbContactTestData.deleteAllContactsFromDB()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbContactTestData.deleteAllContactsFromDB`
      );
    }
  );
  await Promise.all([
    RegistryDbEtsTransactionsTestData.restoreAllocationYearsInDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbEtsTransactionsTestData.restoreAllocationYearsInDB`
    );
  });
  await Promise.all([
    RegistryDbReconciliationFailedEntryTestData.deleteAllReconciliationsFailedEntryFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbReconciliationFailedEntryTestData.deleteAllReconciliationsFailedEntryFromDB`
    );
  });
  await Promise.all([
    RegistryDbReconciliationHistoryTestData.deleteAllReconciliationsHistoryFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbReconciliationHistoryTestData.deleteAllReconciliationsHistoryFromDB`
    );
  });
  await Promise.all([
    RegistryDbReconciliationTestData.deleteAllReconciliationsFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbReconciliationTestData.deleteAllReconciliationsFromDB`
    );
  });
  await Promise.all([
    RegistryDbDomainEventTestData.deleteDomainEventInDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbDomainEventTestData.deleteDomainEventInDB`
    );
  });
  await Promise.all([
    RegistryDbAcceptMessageLogTestData.deleteAllAcceptMessageLogFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbAcceptMessageLogTestData.deleteAllAcceptMessageLogFromDB`
    );
  });
  await Promise.all([
    RegistryDbItlNotificationBlockTestData.deleteAllItlNotificationBlockFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbItlNotificationBlockTestData.deleteAllItlNotificationBlockFromDB`
    );
  });
  await Promise.all([
    RegistryDbItlNotificationHistoryTestData.deleteAllItlNotificationHistoryFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbItlNotificationHistoryTestData.deleteAllItlNotificationHistoryFromDB`
    );
  });
  await Promise.all([
    RegistryDbItlNotificationTestData.deleteAllItlNotificationFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbItlNotificationTestData.deleteAllItlNotificationFromDB`
    );
  });
  await Promise.all([
    RegistryDbEmissionsEntryTestData.deleteAllEmissionsEntryFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbEmissionsEntryTestData.deleteAllEmissionsEntryFromDB`
    );
  });
  await Promise.all([
    RegistryDbExcludeEmissionsEntryTestData.deleteAllExcludeEmissionsEntryFromDB(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbExcludeEmissionsEntryTestData.deleteAllExcludeEmissionsEntryFromDB`
    );
  });
  console.log(`Exiting 'registryDbResetTestData' function.`);
}

export async function registryDbResetAllIndices() {
  console.log(`Entered 'registryDbResetAllIndices'.`);
  await Promise.all([
    RegistryDbInstallationOwnershipTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbInstallationOwnershipTestData.resetIndices`
    );
  });
  await Promise.all([RegistryDbAccountOwnershipTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAccountOwnershipTestData.resetIndices`
      );
    }
  );
  await Promise.all([
    RegistryDbTrustedRegistryDbAccountTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTrustedRegistryDbAccountTestData.resetIndices`
    );
  });
  await Promise.all([RegistryDbAccountAccessTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAccountAccessTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbAccountTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAccountTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbAccountHolderRepTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAccountHolderRepTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbAccountHolderTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAccountHolderTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbUserTestData.resetIndices()]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbUserTestData.resetIndices`
    );
  });
  await Promise.all([RegistryDbUserFilesTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbUserFilesTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbCompliantEntityTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbCompliantEntityTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbContactTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbContactTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbRegistryLevelTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbRegistryLevelTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbTaskSearchMetadataTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbTaskSearchMetadataTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbTaskTestData.resetIndices()]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTaskTestData.resetIndices`
    );
  });
  await Promise.all([
    RegistryDbTransactionConnectionTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionConnectionTestData.resetIndices`
    );
  });
  await Promise.all([
    RegistryDbTransactionResponseTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbTransactionResponseTestData.resetIndices`
    );
  });
  await Promise.all([RegistryDbTransactionBlockTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbTransactionBlockTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbTransactionHistoryTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbTransactionHistoryTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbTransactionMessageTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbTransactionMessageTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbTransactionTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbTransactionTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbTransactionBlockTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbTransactionBlockTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbUnitBlockTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbUnitBlockTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbEtsTransactionsTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbEtsTransactionsTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbAllocationEntryTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAllocationEntryTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbAllocationStatusTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAllocationStatusTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbAllocationPhaseTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAllocationPhaseTestData.resetIndices`
      );
    }
  );
  await Promise.all([
    RegistryDbReconciliationFailedEntryTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbReconciliationFailedEntryTestData.resetIndices`
    );
  });
  await Promise.all([
    RegistryDbReconciliationHistoryTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbReconciliationHistoryTestData.resetIndices`
    );
  });
  await Promise.all([RegistryDbReconciliationTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbReconciliationTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbDomainEventTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbDomainEventTestData.resetIndices`
      );
    }
  );
  await Promise.all([RegistryDbAcceptMessageLogTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbAcceptMessageLogTestData.resetIndices`
      );
    }
  );
  await Promise.all([
    RegistryDbItlNotificationBlockTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbItlNotificationBlockTestData.resetIndices`
    );
  });
  await Promise.all([RegistryDbItlNotificationTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbItlNotificationTestData.resetIndices`
      );
    }
  );
  await Promise.all([
    RegistryDbItlNotificationHistoryTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbItlNotificationHistoryTestData.resetIndices`
    );
  });
  await Promise.all([RegistryDbEmissionsEntryTestData.resetIndices()]).then(
    (result) => {
      console.log(
        `${result} promise resolved for RegistryDbEmissionsEntryTestData.resetIndices`
      );
    }
  );
  await Promise.all([
    RegistryDbExcludeEmissionsEntryTestData.resetIndices(),
  ]).then((result) => {
    console.log(
      `${result} promise resolved for RegistryDbExcludeEmissionsEntryTestData.resetIndices`
    );
  });
  console.log(`Exiting 'registryDbResetAllIndices'.`);
}
