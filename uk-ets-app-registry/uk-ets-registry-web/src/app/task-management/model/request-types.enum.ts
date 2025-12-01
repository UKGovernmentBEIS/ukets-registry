import { TaskType } from '@task-management/model/task-details.model';

/**
 * Task request Type
 */
export enum RequestType {
  /**
   * The account opening request.
   */
  ACCOUNT_OPENING_REQUEST = 'ACCOUNT_OPENING_REQUEST',

  ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST = 'ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST',

  /**
   * The add trusted account request.
   */
  ADD_TRUSTED_ACCOUNT_REQUEST = 'ADD_TRUSTED_ACCOUNT_REQUEST',

  /**
   * The delete trusted account request.
   */
  DELETE_TRUSTED_ACCOUNT_REQUEST = 'DELETE_TRUSTED_ACCOUNT_REQUEST',

  /**
   * The send enrolment key request.
   */
  PRINT_ENROLMENT_LETTER_REQUEST = 'PRINT_ENROLMENT_LETTER_REQUEST',

  /**
   * The transaction request.
   */
  TRANSACTION_REQUEST = 'TRANSACTION_REQUEST',

  /**
   * The request to update transaction rules in account.
   */
  TRANSACTION_RULES_UPDATE_REQUEST = 'TRANSACTION_RULES_UPDATE_REQUEST',

  /**
   * The requests to manage authorise representatives updates in account
   */
  AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST = 'AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST',
  AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST = 'AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST',
  AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST = 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST',
  AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST = 'AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST',
  AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST = 'AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST',
  AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST = 'AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST',

  /**
   * The request to upload the allocation table
   */
  ALLOCATION_TABLE_UPLOAD_REQUEST = 'ALLOCATION_TABLE_UPLOAD_REQUEST',

  ALLOCATION_REQUEST = 'ALLOCATION_REQUEST',

  AH_REQUESTED_DOCUMENT_UPLOAD = 'AH_REQUESTED_DOCUMENT_UPLOAD',

  AR_REQUESTED_DOCUMENT_UPLOAD = 'AR_REQUESTED_DOCUMENT_UPLOAD',

  /**
   * The request types to manage the account holder updates
   */
  ACCOUNT_HOLDER_UPDATE_DETAILS = 'ACCOUNT_HOLDER_UPDATE_DETAILS',
  ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS = 'ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS',
  ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD = 'ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD',
  ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE = 'ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE',
  ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE = 'ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE',

  /**
   * Request to change the 2nd factor of authentication.
   */
  CHANGE_TOKEN = 'CHANGE_TOKEN',

  /**
   * The request to upload the emissions table
   */
  EMISSIONS_TABLE_UPLOAD_REQUEST = 'EMISSIONS_TABLE_UPLOAD_REQUEST',

  /**
   * Request due to loss of the 2nd factor of authentication (panic button).
   */
  LOST_TOKEN = 'LOST_TOKEN',
  /**
   * Request to change email
   */
  REQUESTED_EMAIL_CHANGE = 'REQUESTED_EMAIL_CHANGE',
  /**
   * Request due to loss of both the password and the 2nd factor of authentication (panic button).
   */
  LOST_PASSWORD_AND_TOKEN = 'LOST_PASSWORD_AND_TOKEN',

  /**
   * Request to update the installation operator info.
   */
  INSTALLATION_OPERATOR_UPDATE_REQUEST = 'INSTALLATION_OPERATOR_UPDATE_REQUEST',

  /**
   * Request to update the aircraft operator info.
   */
  AIRCRAFT_OPERATOR_UPDATE_REQUEST = 'AIRCRAFT_OPERATOR_UPDATE_REQUEST',

  /**
   * Request to update the maritime operator info.
   */
  MARITIME_OPERATOR_UPDATE_REQUEST = 'MARITIME_OPERATOR_UPDATE_REQUEST',

  ACCOUNT_TRANSFER = 'ACCOUNT_TRANSFER',

  /**
   * Request to update the personal and work details of the user.
   */
  USER_DETAILS_UPDATE_REQUEST = 'USER_DETAILS_UPDATE_REQUEST',

  /**
   * Request to deactivate a user.
   */
  USER_DEACTIVATION_REQUEST = 'USER_DEACTIVATION_REQUEST',

  /**
   * Request to close an account.
   */
  ACCOUNT_CLOSURE_REQUEST = 'ACCOUNT_CLOSURE_REQUEST',

  /**
   * Request for payment.
   */
  PAYMENT_REQUEST = 'PAYMENT_REQUEST',

  /**
   * ACCOUNT_HOLDER_CHANGE
   */
  ACCOUNT_HOLDER_CHANGE = 'ACCOUNT_HOLDER_CHANGE',
}

interface RequestTypeValues {
  label: string;
  headingText: string;
  approvalText: string;
  rejectionText: string;
  confirmationText: string;
  /**
   * Decides if after completing the task, the user is navigated to a confirmation page,
   * instead of the task details
   */
  goToConfirmationPageAfterCompletion: boolean;
  /**
   * Indicates that this task type requires 2FA on approval
   */
  requiresOtpVerificationOnApproval: boolean;
  /**
   * This is a complete only task - no approve/reject actions available - just complete task
   */
  completeOnly: boolean;
}

export const REQUEST_TYPES_CAUSING_USER_SUSPENSION = [
  RequestType.LOST_PASSWORD_AND_TOKEN,
  RequestType.LOST_TOKEN,
  RequestType.CHANGE_TOKEN,
];

export const REQUEST_TYPE_VALUES: Record<RequestType, RequestTypeValues> = {
  ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS: {
    label: 'Update the Primary Contact details',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting this request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD: {
    label: 'Add the alternative Primary Contact',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting this request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE: {
    label: 'Remove the alternative Primary Contact',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting this request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE: {
    label: 'Update the alternative Primary Contact',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting this request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_HOLDER_UPDATE_DETAILS: {
    label: 'Update the account holder details',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting this request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_OPENING_REQUEST: {
    label: 'Open Account',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the account opening request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST: {
    label: 'Open Account with installation transfer',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the account request',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ADD_TRUSTED_ACCOUNT_REQUEST: {
    label: 'Add trusted account',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AH_REQUESTED_DOCUMENT_UPLOAD: {
    label: 'Submit documents for account holder',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Enter the information requested and select "Complete task"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: true,
  },
  ALLOCATION_REQUEST: {
    label: 'Allocate allowances proposal',
    headingText: 'proposal',
    approvalText: 'Explain why you are approving this proposal (optional)',
    rejectionText: 'Why are you rejecting this proposal ?',
    confirmationText: 'Check the allocation proposal',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ALLOCATION_TABLE_UPLOAD_REQUEST: {
    label: 'Upload allocation table',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AR_REQUESTED_DOCUMENT_UPLOAD: {
    label: 'Submit documents for user',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Enter the information requested and select "Complete task"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: true,
  },
  AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST: {
    label: 'Add authorised representative',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST: {
    label: 'Remove authorised representative',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST: {
    label: 'Replace authorised representative',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST: {
    label: 'Restore authorised representative',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST: {
    label: 'Suspend authorised representative',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST: {
    label: 'Change authorised representatives permissions',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  CHANGE_TOKEN: {
    label: 'Approve two factor authentication change request',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  DELETE_TRUSTED_ACCOUNT_REQUEST: {
    label: 'Remove trusted account',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  EMISSIONS_TABLE_UPLOAD_REQUEST: {
    label: 'Approve emission table',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: true,
    completeOnly: false,
  },
  LOST_TOKEN: {
    label: 'Request two factor authentication emergency access',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  PRINT_ENROLMENT_LETTER_REQUEST: {
    label: 'Print letter with registry activation code',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Download the letter and select "Complete task"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: true,
  },
  REQUESTED_EMAIL_CHANGE: {
    label: 'Approve email address change request',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting the request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  TRANSACTION_REQUEST: {
    label: 'Transaction Proposal',
    headingText: 'proposal',
    approvalText: 'Explain why you are approving this proposal (optional)',
    rejectionText: 'Why are you rejecting this proposal ?',
    confirmationText: 'Check the transaction proposal',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: true,
    completeOnly: false,
  },
  TRANSACTION_RULES_UPDATE_REQUEST: {
    label: 'Update transaction rules',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText:
      'Check the update request and select either "Approve" or "Reject"',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  LOST_PASSWORD_AND_TOKEN: {
    label: 'Request password and two factor authentication emergency access',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  INSTALLATION_OPERATOR_UPDATE_REQUEST: {
    label: 'Update account installation details',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting the request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  AIRCRAFT_OPERATOR_UPDATE_REQUEST: {
    label: 'Update account aircraft operator details',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting the request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  MARITIME_OPERATOR_UPDATE_REQUEST: {
    label: 'Update account maritime operator details',
    headingText: 'request',
    approvalText: 'Enter comment (optional)',
    rejectionText: 'Enter the reason for rejecting the request',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_TRANSFER: {
    label: 'Account transfer',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  USER_DETAILS_UPDATE_REQUEST: {
    label: 'Update user details',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  USER_DEACTIVATION_REQUEST: {
    label: 'Deactivate user',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_CLOSURE_REQUEST: {
    label: 'Close account',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: true,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  PAYMENT_REQUEST: {
    label: 'Payment request',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request',
    rejectionText: 'Why are you rejecting this request ?',
    confirmationText: 'Check the update request',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
  ACCOUNT_HOLDER_CHANGE: {
    label: 'Change account holder',
    headingText: 'request',
    approvalText: 'Explain why you are approving this request (optional)',
    rejectionText: 'Why are you rejecting this request?',
    confirmationText:
      'Check the request and select either “Approve” or “Reject”',
    goToConfirmationPageAfterCompletion: false,
    requiresOtpVerificationOnApproval: false,
    completeOnly: false,
  },
};

function addRecordValuesToTaskTypeArray(): TaskType[] {
  const arr: TaskType[] = [];
  Object.entries(REQUEST_TYPE_VALUES).forEach(([request_type, textValues]) =>
    arr.push({ label: textValues.label, value: request_type })
  );
  return arr;
}

export const TASK_TYPE_OPTIONS: TaskType[] = [
  {
    label: '',
    value: null,
  },
].concat(addRecordValuesToTaskTypeArray());
