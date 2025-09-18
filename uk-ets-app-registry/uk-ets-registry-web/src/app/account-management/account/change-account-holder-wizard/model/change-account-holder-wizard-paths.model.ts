export enum ChangeAccountHolderWizardPathsModel {
  BASE_PATH = 'change-account-holder',
  ACCOUNT_HOLDER_SELECTION = 'selection',
  INDIVIDUAL = 'individual',
  INDIVIDUAL_DETAILS = 'individual/details',
  INDIVIDUAL_CONTACT_DETAILS = 'individual/contact',
  ORGANISATION = 'organisation',
  ORGANISATION_DETAILS = 'organisation/details',
  ORGANISATION_ADDRESS_DETAILS = 'organisation/address',
  PRIMARY_CONTACT = 'primary-contact',
  PRIMARY_CONTACT_WORK = 'primary-contact-work',

  UPDATE_AH_ADDRESS = 'update-account-holder-address',
  CHECK_CHANGE_ACCOUNT_HOLDER = 'check-change-account-holder',
  CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST = 'cancel',
  REQUEST_SUBMITTED = 'request-submitted',
}
