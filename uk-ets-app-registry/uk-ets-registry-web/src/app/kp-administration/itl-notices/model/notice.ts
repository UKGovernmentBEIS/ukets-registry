import { Status } from '@shared/model/status';

export type NoticeStatus =
  | 'TRANSACTION_PROPOSAL_PENDING'
  | 'TRANSACTION_APPROVAL_PENDING'
  | 'ITL_UPDATE_PENDING'
  | 'OPEN'
  | 'INCOMPLETE'
  | 'COMPLETED';

export type NoticeType =
  | 'NET_SOURCE_CANCELLATION'
  | 'NON_COMPLIANCE_CANCELLATION'
  | 'IMPENDING_EXPIRY_OF_TCER_AND_LCER'
  | 'REVERSAL_OF_STORAGE_FOR_CDM_PROJECT'
  | 'NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT'
  | 'EXCESS_ISSUANCE_FOR_CDM_PROJECT'
  | 'COMMITMENT_PERIOD_RESERVE'
  | 'EXPIRY_DATE_CHANGE'
  | 'UNIT_CARRY_OVER'
  | 'NOTIFICATION_UPDATE'
  | 'EU15_COMMITMENT_PERIOD_RESERVE'
  | 'NET_REVERSAL_OF_STORAGE_OF_A_CDM_CCS_PROJECT'
  | 'NON_SUBMISSION_OF_VERIFICATION_REPORT_FOR_A_CDM_CCS_PROJECT';

export interface Notice {
  id: number;
  actionDueDate: Date;
  commitPeriod: number;
  content: string;
  lulucfactivity: string;
  messageDate: Date;
  status: NoticeStatus;
  type: NoticeType;
  notificationIdentifier: number;
  projectNumber: string;
  targetDate: Date;
  targetValue: number;
  unitBlockIdentifiers: [];
  unitType: number;
  receivedOn: Date;
  lastUpdateOn: Date;
  createdDate: Date;
}

export const noticeTypeMap: Record<NoticeType, string> = {
  NET_SOURCE_CANCELLATION: 'Type 1 - Net source cancellation',
  NON_COMPLIANCE_CANCELLATION: 'Type 2 - Non-compliance cancellation',
  IMPENDING_EXPIRY_OF_TCER_AND_LCER: 'Type 3 - Impending tCER/lCER expiry',
  REVERSAL_OF_STORAGE_FOR_CDM_PROJECT:
    'Type 4 - Reversal of storage for CDM project',
  NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT:
    'Type 5 - Non-submission of certification report for CDM project',
  EXCESS_ISSUANCE_FOR_CDM_PROJECT: 'Type 6 - Excess issuance for CDM project',
  COMMITMENT_PERIOD_RESERVE: 'Type 7 - Commitment Period reserve',
  UNIT_CARRY_OVER: 'Type 8 - Unit carry-over',
  EXPIRY_DATE_CHANGE: 'Type 9 - Expiry date change',
  NOTIFICATION_UPDATE: 'Type 10 - Notification update',
  NET_REVERSAL_OF_STORAGE_OF_A_CDM_CCS_PROJECT:
    'Type 12 - Net reversal of storage of a CDM CCS project',
  NON_SUBMISSION_OF_VERIFICATION_REPORT_FOR_A_CDM_CCS_PROJECT:
    'Type 13 - Non-submission of verification report for a CDM CCS project',
  EU15_COMMITMENT_PERIOD_RESERVE: 'Type 14 - EU15 Commitment Period reserve',
};

export const noticeStatusMap: Record<NoticeStatus, Status> = {
  TRANSACTION_PROPOSAL_PENDING: {
    label: 'Transaction Proposal pending',
    color: 'red',
  },
  TRANSACTION_APPROVAL_PENDING: {
    label: 'Transaction Approval pending',
    color: 'yellow',
  },
  ITL_UPDATE_PENDING: { label: 'ITL Update pending', color: 'blue' },
  OPEN: {
    label: 'Open',
    color: 'red',
  },
  INCOMPLETE: { label: 'Incomplete', color: 'blue' },
  COMPLETED: { label: 'Completed', color: 'green' },
};
