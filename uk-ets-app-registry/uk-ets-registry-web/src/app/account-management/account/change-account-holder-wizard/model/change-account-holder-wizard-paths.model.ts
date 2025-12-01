export const CHANGE_ACCOUNT_HOLDER_BASE_PATH = 'change-account-holder';

export enum ChangeAccountHolderWizardPaths {
  SELECT_TYPE = 'select-organisation-or-individual',
  SELECT_EXISTING_OR_ADD_NEW = 'select-existing-or-add-new',
  INDIVIDUAL_DETAILS = 'individual-details',
  INDIVIDUAL_CONTACT = 'individual-contact',
  ORGANISATION_DETAILS = 'organisation-details',
  ORGANISATION_ADDRESS = 'organisation-address',
  PRIMARY_CONTACT = 'primary-contact',
  PRIMARY_CONTACT_WORK = 'primary-contact-work',
  DELETE_ORPHAN_ACCOUNT_HOLDER = 'delete-account-holder',

  OVERVIEW = 'check-change-account-holder',
  CANCEL_REQUEST = 'cancel',
  REQUEST_SUBMITTED = 'request-submitted',
}
