export enum AccountHolderDetailsWizardPathsModel {
  BASE_PATH = 'account-holder-details-update',
  SELECT_UPDATE_TYPE = 'select-update-type',
  UPDATE_ACCOUNT_HOLDER = 'update-account-holder',
  UPDATE_AH_ADDRESS = 'update-account-holder-address',
  UPDATE_PRIMARY_CONTACT = 'update-primary-contact',
  UPDATE_PRIMARY_CONTACT_WORK = 'update-primary-contact-work',
  UPDATE_ALTERNATIVE_PRIMARY_CONTACT = 'update-alternative-primary-contact',
  UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK = 'update-alternative-primary-contact-work',
  CHECK_UPDATE_REQUEST = 'check-update-request',
  CANCEL_UPDATE_REQUEST = 'cancel',
  REQUEST_SUBMITTED = 'request-submitted',
}

export const AccountHolderDetailsWizardPathsMap = new Map<
  string,
  AccountHolderDetailsWizardPathsModel
>([
  [
    'account-holder-details-update',
    AccountHolderDetailsWizardPathsModel.BASE_PATH,
  ],
  [
    'select-update-type',
    AccountHolderDetailsWizardPathsModel.SELECT_UPDATE_TYPE,
  ],
  [
    'update-account-holder',
    AccountHolderDetailsWizardPathsModel.UPDATE_ACCOUNT_HOLDER,
  ],
  [
    'update-account-holder-address',
    AccountHolderDetailsWizardPathsModel.UPDATE_AH_ADDRESS,
  ],
  [
    'update-primary-contact',
    AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT,
  ],
  [
    'update-primary-contact-work',
    AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT_WORK,
  ],
  [
    'update-alternative-primary-contact',
    AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT,
  ],
  [
    'update-alternative-primary-contact-work',
    AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK,
  ],
  [
    'check-update-request',
    AccountHolderDetailsWizardPathsModel.CHECK_UPDATE_REQUEST,
  ],
  ['cancel', AccountHolderDetailsWizardPathsModel.CANCEL_UPDATE_REQUEST],
  ['request-submitted', AccountHolderDetailsWizardPathsModel.REQUEST_SUBMITTED],
]);
