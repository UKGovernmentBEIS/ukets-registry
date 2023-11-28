export enum AccountTransferPathsModel {
  BASE_PATH = 'account-transfer',
  SELECT_UPDATE_TYPE = 'select-update-type',
  UPDATE_ACCOUNT_HOLDER = 'update-account-holder',
  UPDATE_AH_ADDRESS = 'update-account-holder-address',
  UPDATE_PRIMARY_CONTACT = 'update-primary-contact',
  UPDATE_PRIMARY_CONTACT_WORK = 'update-primary-contact-work',
  CHECK_ACCOUNT_TRANSFER = 'check-account-transfer',
  CANCEL_ACCOUNT_TRANSFER_REQUEST = 'cancel',
  REQUEST_SUBMITTED = 'request-submitted',
}

export const AccountTransferPathsMap = new Map<
  string,
  AccountTransferPathsModel
>([
  ['account-transfer', AccountTransferPathsModel.BASE_PATH],
  ['select-update-type', AccountTransferPathsModel.SELECT_UPDATE_TYPE],
  ['update-account-holder', AccountTransferPathsModel.UPDATE_ACCOUNT_HOLDER],
  [
    'update-account-holder-address',
    AccountTransferPathsModel.UPDATE_AH_ADDRESS,
  ],
  ['update-primary-contact', AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT],
  [
    'update-primary-contact-work',
    AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT_WORK,
  ],
  ['check-account-transfer', AccountTransferPathsModel.CHECK_ACCOUNT_TRANSFER],
  ['cancel', AccountTransferPathsModel.CANCEL_ACCOUNT_TRANSFER_REQUEST],
  ['request-submitted', AccountTransferPathsModel.REQUEST_SUBMITTED],
]);
