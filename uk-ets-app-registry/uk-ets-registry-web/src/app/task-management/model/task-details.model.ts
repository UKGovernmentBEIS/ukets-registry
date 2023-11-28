import { AccountRepresentation } from '@account-opening/account-opening.service';
import { RequestType } from './request-types.enum';
import { AccountInfo, TransactionType } from '@shared/model/transaction';
import {
  AccountDetails,
  AccountHolder,
  AccountHolderContact,
  ARAccessRights,
  AuthorisedRepresentative,
  Installation,
  Operator,
  TrustedAccount,
  TrustedAccountListRules,
} from '@shared/model/account';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { DomainEvent } from '@shared/model/event';
import { AllocationOverview } from '@shared/model/allocation';
import { FileDetails } from '@shared/model/file/file-details.model';
import { AccountHolderInfoChanged } from '@account-management/account/account-holder-details-wizard/model';
import { Status } from '@shared/model/status';
import { FileBase } from '@shared/model/file';
import { IUser } from '@registry-web/shared/user';
import { KeycloakUser } from '@shared/user';
import { UserDeactivationDetails } from '@registry-web/user-management/user-details/model/user-deactivation-details';
import { AllocationStatus } from '@account-management/account-list/account-list.model';

export interface TaskDetailsBase {
  requestId: string;
  initiatorName: string;
  initiatorUrid: string;
  claimantName: string;
  claimantURID: string;
  taskStatus: string;
  requestStatus: TaskOutcome;
  initiatedDate: string;
  claimedDate: string;
  currentUserClaimant: boolean;
  completedByName: string;
  accountNumber: string;
  accountFullIdentifier: string;
  accountName: string;
  referredUserFirstName: string;
  referredUserLastName: string;
  referredUserURID: string;
  history: DomainEvent[];
  subTasks: TaskDetailsBase[];
  parentTask: TaskDetailsBase;
}

export interface AccountOpeningTaskDetails extends TaskDetailsBase {
  taskType:
    | RequestType.ACCOUNT_OPENING_REQUEST
    | RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST;
  account: AccountRepresentation;
  userDetails?: KeycloakUser[];
}

export interface EnrolmentLetterTaskDetails extends TaskDetailsBase {
  taskType: RequestType.PRINT_ENROLMENT_LETTER_REQUEST;
  fileName: string;
  fileSize: string;
}

export interface TrustedAccountTaskDetails extends TaskDetailsBase {
  taskType:
    | RequestType.ADD_TRUSTED_ACCOUNT_REQUEST
    | RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST;
  accountInfo: AccountInfo;
  trustedAccounts: TrustedAccount[];
}

export interface TransactionRuleUpdateTaskDetails extends TaskDetailsBase {
  taskType: RequestType.TRANSACTION_RULES_UPDATE_REQUEST;
  accountInfo: AccountInfo;
  trustedAccountListRules: TrustedAccountListRules;
}

export interface OperatorUpdateTaskDetails extends TaskDetailsBase {
  taskType:
    | RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST
    | RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST;
  accountInfo: AccountInfo;
  current: Operator;
  changed: Operator;
}

export interface AuthoriseRepresentativeTaskDetails extends TaskDetailsBase {
  taskType:
    | RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST
    | RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST
    | RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST
    | RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST
    | RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST
    | RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST;
  accountInfo: AccountInfo;
  arUpdateType: AuthorisedRepresentativesUpdateType;
  arUpdateAccessRight: ARAccessRights;
  newUser: AuthorisedRepresentative;
  currentUser: AuthorisedRepresentative;
  userDetails?: KeycloakUser[];
}

export interface AllocationTableUploadTaskDetails extends TaskDetailsBase {
  taskType: RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST;
  fileName: string;
  fileSize: string;
}

export interface EmissionsTableUploadTaskDetails extends TaskDetailsBase {
  taskType: RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST;
  fileName: string;
  fileSize: string;
}

export interface TransactionTaskDetailsBase extends TaskDetailsBase {
  taskType: RequestType.TRANSACTION_REQUEST;
  // rename to transactionType after renaming conflicting property transactionType in generic transaction
  trType: TransactionType;
  reference?: string;
  natTransactionIdentifier?: string;
  nerTransactionIdentifier?: string;
}

export interface AllocationRequestTaskDetails extends TaskDetailsBase {
  taskType: RequestType.ALLOCATION_REQUEST;
  allocationOverview: AllocationOverview;
  natAccountName: string;
  nerAccountName: string;
  currentHoldings: number;
  nerCurrentHoldings?: number;
}

export interface RequestedDocumentUploadTaskDetails extends TaskDetailsBase {
  taskType:
    | RequestType.AR_REQUESTED_DOCUMENT_UPLOAD
    | RequestType.AH_REQUESTED_DOCUMENT_UPLOAD;
  //only used for AH_REQUESTED_DOCUMENT_UPLOADS
  accountHolderName: string;
  //only used for AR_REQUESTED_DOCUMENT_UPLOADS
  recipient: string;
  userUrid: string;
  accountHolderIdentifier: string;

  documentNames: string[];
  // documentNameIdInfoList: [{ documentName: string, documentId: string }],
  reasonForAssignment: string;
  comment: string;
  referenceFiles: FileDetails[];
  uploadedFiles: FileBase[];
  completedDate: Date;
  difference: string;
}

export interface AccountHolderUpdateDetails extends TaskDetailsBase {
  taskType: RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS;
  accountDetails: AccountDetails;
  accountHolderOwnership: AccountHolderOwnership[];
  accountHolder: AccountHolder;
  accountHolderDiff: AccountHolderInfoChanged;
}

export interface AccountHolderPrimaryContactUpdateDetails
  extends TaskDetailsBase {
  taskType:
    | RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS
    | RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE
    | RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD;
  accountDetails: AccountDetails;
  accountHolderOwnership: AccountHolderOwnership[];
  accountHolderContact: AccountHolderContact;
  accountHolderContactDiff: AccountHolderContact;
}

export interface ChangeTokenTaskDetails extends TaskDetailsBase {
  taskType: RequestType.CHANGE_TOKEN;
  comment: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface LostTokenTaskDetails extends TaskDetailsBase {
  taskType: RequestType.LOST_TOKEN | RequestType.LOST_PASSWORD_AND_TOKEN;
  comment: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface EmailChangeTaskDetails extends TaskDetailsBase {
  userLastName: string;
  userFirstName: string;
  userCurrentEmail: string;
  userNewEmail: string;
  userUrid: string;
}

export interface AccountTransferAction {
  accountHolderDTO: AccountHolder;
  accountHolderContactInfo: AccountHolderContact;
  installationDetails?: Installation;
}

export interface AccountTransferTaskDetails extends TaskDetailsBase {
  taskType: RequestType.ACCOUNT_TRANSFER;
  action: AccountTransferAction;
  currentAccountHolder: AccountHolder;
  account: AccountDetails;
}

export interface AccountClosureTaskDetails extends TaskDetailsBase {
  taskType: RequestType.ACCOUNT_CLOSURE_REQUEST;
  accountDetails: AccountDetails;
  permitId: string;
  monitoringPlanId: string;
  closureComment: string;
  allocationClassification: AllocationStatus;
  noActiveAR: boolean;
  pendingAllocationTaskExists: boolean;
}

export interface UserDetailsUpdateTaskDetails extends TaskDetailsBase {
  taskType: RequestType.USER_DETAILS_UPDATE_REQUEST;
  current: IUser;
  changed: IUser;
  userDetails?: KeycloakUser[];
}

export interface UserDeactivationTaskDetails extends TaskDetailsBase {
  taskType: RequestType.USER_DEACTIVATION_REQUEST;
  changed: UserDeactivationDetails;
}

export type TaskDetails =
  | AccountOpeningTaskDetails
  | TransactionTaskDetailsBase
  | EnrolmentLetterTaskDetails
  | TrustedAccountTaskDetails
  | TransactionRuleUpdateTaskDetails
  | AuthoriseRepresentativeTaskDetails
  | AllocationTableUploadTaskDetails
  | AllocationRequestTaskDetails
  | EmissionsTableUploadTaskDetails
  | RequestedDocumentUploadTaskDetails
  | AccountHolderUpdateDetails
  | AccountHolderPrimaryContactUpdateDetails
  | ChangeTokenTaskDetails
  | LostTokenTaskDetails
  | OperatorUpdateTaskDetails
  | AccountTransferTaskDetails
  | AccountClosureTaskDetails
  | UserDetailsUpdateTaskDetails
  | UserDeactivationTaskDetails;

export enum TaskOutcome {
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  SUBMITTED_NOT_YET_APPROVED = 'SUBMITTED_NOT_YET_APPROVED',
}

export interface TaskType {
  label: string;
  value: string;
}

export interface AccountType {
  label: string;
  value: string;
}

export interface AccountHolderOwnership {
  accountType: string;
  numberOfOwnedAccounts: string;
}

export const requestStatusMap: Record<TaskOutcome, Status> = {
  SUBMITTED_NOT_YET_APPROVED: { color: 'green', label: 'Submitted' },
  APPROVED: { color: 'green', label: 'Approved' },
  REJECTED: { color: 'red', label: 'Rejected' },
};

export interface TaskFileDownloadInfo {
  fileId?: number;
  taskType: RequestType;
  taskRequestId: string;
}

export interface TaskUpdateDetails {
  updateInfo: string;
  taskUpdateAction: TaskUpdateAction;
  taskDetails: TaskDetails;
}

export enum TaskUpdateAction {
  CHANGE_REGULATOR = 'CHANGE_REGULATOR',
  UPDATE_ACCOUNT_HOLDER = 'UPDATE_ACCOUNT_HOLDER',
  RESET_ACCOUNT_HOLDER = 'RESET_ACCOUNT_HOLDER',
  RESET_REGULATOR = 'RESET_REGULATOR',
}
