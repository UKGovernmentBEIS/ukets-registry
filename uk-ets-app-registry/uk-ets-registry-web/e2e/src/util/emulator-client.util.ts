import { HttpClient } from 'protractor-http-client/dist/http-client';
import { environment } from './environment-configuration';
import { getMapAttributeByAttribute } from './step.util';

const httpClient = new HttpClient(environment().emulatorUrl);

export interface EmulatorConfiguration {
  itlStatus?: string;
  soapMessageAccepted?: number;
  responseCode?: string;
  delayInMs?: number;
}

export function configureEmulator(configuration: EmulatorConfiguration) {
  httpClient.put('/config', configuration).then(
    () => {
      console.log(`Configuration updated with: ${configuration}`);
    },
    (error) => {
      console.log(`Configuration was not updated, error is: ${error}`);
    }
  );
}

export interface ProposalUnitBlock {
  unitSerialBlockStart: number;
  unitSerialBlockEnd: number;
  originatingRegistryCode: string;
  unitType: number;
  suppUnitType?: number;
  originalCommitPeriod: number;
  applicableCommitPeriod: number;
  LULUCFActivity?: number;
  projectIdentifier?: number;
  track?: number;
  blockRole?: string;
  transferringRegistryAccountType?: number;
  transferringRegistryAccountIdentifier?: number;
  acquiringRegistryAccountType?: number;
  acquiringRegistryAccountIdentifier?: number;
  yearInCommitmentPeriod?: number;
  installationIdentifier?: number;
  expiryDate?: string;
}

export interface ProposalTransaction {
  transactionIdentifier: string;
  transactionType: number;
  suppTransactionType?: number;
  transferringRegistryCode: string;
  transferringRegistryAccountType?: number;
  transferringRegistryAccountIdentifier?: number;
  acquiringRegistryCode: string;
  acquiringRegistryAccountType?: number;
  acquiringRegistryAccountIdentifier?: number;
  notificationIdentifier?: number;
  proposalUnitBlocks: [ProposalUnitBlock];
}

export interface ProposalRequest {
  from: string;
  to: string;
  majorVersion: number;
  minorVersion: number;
  proposedTransaction: ProposalTransaction;
}

export async function submitProposalRequest(proposalRequest: ProposalRequest) {
  await httpClient.post('/transactionProposal', proposalRequest).then(
    (success) => {
      console.log(`Proposal request submitted: ${JSON.stringify(success)}`);
    },
    (error) => {
      console.log(
        `Proposal was not submitted, error is: ${JSON.stringify(error)}`
      );
    }
  );
}

export async function submitItlNotificationRequest(
  notificationIdentifier: string,
  notificationType: string,
  notificationStatus: string
) {
  const content = {
    from: 'ITL',
    to: 'GB',
    majorVersion: 3,
    minorVersion: 3,
    messageContent: 'A message from Me.',
    messageDate: '2020-11-13T14:21:14+02:00',
    notificationType: notificationType,
    notificationIdentifier: notificationIdentifier,
    notificationStatus: notificationStatus,
    projectNumber: '10',
    unitType: 1,
    targetValue: 1,
    targetDate: '2020-11-13T14:21:14+02:00',
    LULUCFActivity: 0,
    commitPeriod: 1,
    actionDueDate: '2020-11-13T14:21:14+02:00',
    unitBlockIdentifiers: [
      {
        unitSerialBlockStart: 10,
        unitSerialBlockEnd: 1000,
        originatingRegistryCode: '12',
      },
      {
        unitSerialBlockStart: 15,
        unitSerialBlockEnd: 1012,
        originatingRegistryCode: '12',
      },
    ],
  };

  await httpClient.post('/sendNotice', content).then(
    (success) => {
      console.log(
        `\nNotification request submitted: '${JSON.stringify(success)}'.\n`
      );
    },
    (error) => {
      console.log(
        `\nNotification was not submitted, error is: '${JSON.stringify(
          error
        )}'.\n`
      );
    }
  );
}

export async function submitProposalRequestInboundTransaction(
  map: Map<string, string>
) {
  // default values defined in current JSON file, are overridden when respective argument is set
  const tx: ProposalRequest = {
    from: 'ITL',
    to: 'GB',
    majorVersion: 1,
    minorVersion: 1,
    proposedTransaction: {
      transactionIdentifier: 'GR200001',
      transactionType:
        getMapAttributeByAttribute(map, 'value', 'transactionType', 'key') ===
        undefined
          ? 3
          : getMapAttributeByAttribute(map, 'value', 'transactionType', 'key'),
      suppTransactionType:
        getMapAttributeByAttribute(
          map,
          'value',
          'suppTransactionType',
          'key'
        ) === undefined
          ? 0
          : getMapAttributeByAttribute(
              map,
              'value',
              'suppTransactionType',
              'key'
            ),
      transferringRegistryCode: 'GR',
      transferringRegistryAccountType:
        getMapAttributeByAttribute(
          map,
          'value',
          'transferringRegistryAccountType',
          'key'
        ) === undefined
          ? 100
          : getMapAttributeByAttribute(
              map,
              'value',
              'transferringRegistryAccountType',
              'key'
            ),
      transferringRegistryAccountIdentifier:
        getMapAttributeByAttribute(
          map,
          'value',
          'transferringRegistryAccountIdentifier',
          'key'
        ) === undefined
          ? 1000
          : getMapAttributeByAttribute(
              map,
              'value',
              'transferringRegistryAccountIdentifier',
              'key'
            ),
      acquiringRegistryCode: 'GB',
      acquiringRegistryAccountType:
        getMapAttributeByAttribute(
          map,
          'value',
          'acquiringRegistryAccountType',
          'key'
        ) === undefined
          ? 100
          : getMapAttributeByAttribute(
              map,
              'value',
              'acquiringRegistryAccountType',
              'key'
            ),
      acquiringRegistryAccountIdentifier: getMapAttributeByAttribute(
        map,
        'value',
        'acquiringRegistryAccountIdentifier',
        'key'
      ),
      proposalUnitBlocks: [
        {
          unitSerialBlockStart: 200011,
          unitSerialBlockEnd: 200020,
          originatingRegistryCode: 'GR',
          unitType: 1,
          suppUnitType: 0,
          originalCommitPeriod: 1,
          applicableCommitPeriod: 1,
        },
      ],
    },
  };
  await submitProposalRequest(tx);
}
