/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */
import { RegistryScreen } from './screens';

export class AppRoutesPerScreenUtil {
  public static readonly NOT_DEFINED_ROUTE = 'NOT_DEFINED_ROUTE';
  static routesPerScreenName: Map<string, string> = new Map<string, string>()
    .set(RegistryScreen.REQUEST_DOCUMENTS, 'request-documents')
    .set(RegistryScreen.CHANGE_PASSWORD, 'password-change')
    .set(
      RegistryScreen.UPDATE_EXCLUSION_STATUS,
      'account/[identifier]/update-exclusion-status'
    )
    .set(RegistryScreen.PERSONAL_DETAILS, 'registration/personal-details')
    .set(RegistryScreen.WORK_CONTACT_DETAILS, 'registration/work-details')
    .set(RegistryScreen.KYOTO_PROTOCOL_PUBLIC_REPORTS, 'public-reports/kp')
    .set(RegistryScreen.ETS_PUBLIC_REPORTS, 'public-reports/ets')
    .set(RegistryScreen.ACCOUNT_CLOSURE, 'account/[identifier]/account-closure')
    .set(
      RegistryScreen.VIEW_ACCOUNT_OPERATOR_UPDATE,
      'account/[identifier]/operator-update'
    )
    .set(
      RegistryScreen.CHANGE_PASSWORD_CONFIRMATION,
      'password-change/confirmation'
    )
    .set(
      RegistryScreen.KP_ADMIN_ITL_NOTIFICATIONS,
      'kpadministration/itl-notices'
    )
    .set(
      RegistryScreen.KP_ADMIN_ITL_SPECIFIC_NOTIFICATION,
      'kpadministration/itl-notices/[identifier]'
    )
    .set(
      RegistryScreen.KP_ADMIN_ITL_SPECIFIC_NOTIFICATION_TYPE_DETAILS,
      'kpadministration/itl-notices/[itl-notification-identifier]'
    )
    .set(RegistryScreen.CHOOSE_PWD, 'registration/choose-password')
    .set(RegistryScreen.MEMORABLE_PHRASE, 'registration/memorable-phrase')
    .set(RegistryScreen.RESET_YOUR_CREDENTIALS, 'reset-your-credentials')
    .set(RegistryScreen.ENTER_YOUR_NEW_PASSWORD, 'enter-your-new-password')
    .set(RegistryScreen.EMERGENCY_OTP_CHANGE, 'emergency-otp-change/init')
    .set(
      RegistryScreen.SUCCESSFULLY_CHANGED_YOUR_CREDENTIALS,
      'successfully-changed-credentials'
    )
    .set(
      RegistryScreen.EMERGENCY_ACCESS_REQUEST_SUBMITTED,
      'emergency-password-otp-change/email-verify'
    )
    .set(
      RegistryScreen.EMERGENCY_PASSWORD_AND_OTP_CHANGE,
      'emergency-password-otp-change/init'
    )
    .set(
      RegistryScreen.EMERGENCY_OTP_CHANGE_SUBMITTED,
      'emergency-otp-change/email-verify'
    )
    .set(
      RegistryScreen.ETS_ADMIN_PROPOSE_ISSUE_UK_ALLOWANCES,
      'ets-administration/issue-allowances'
    )
    .set(
      RegistryScreen.ETS_ADMIN_ALLOCATION_TABLE,
      'ets-administration/allocation-table'
    )
    .set(
      RegistryScreen.ETS_ADMIN_UPLOAD_EMISSIONS_TABLE,
      'ets-administration/emissions-table'
    )
    .set(
      RegistryScreen.ETS_ADMIN_UPLOAD_EMISSIONS_TABLE_CHECK,
      'ets-administration/emissions-table/check-request-and-submit'
    )
    .set(
      RegistryScreen.ACCOUNT_HOLDER_DETAILS_UPDATE,
      'account/[identifier]/account-holder-details-update'
    )
    .set(RegistryScreen.EMAIL_CHANGE, 'email-change')
    .set(RegistryScreen.EMAIL_CHANGE_VERIFICATION, 'email-change/verification')
    .set(RegistryScreen.TOKEN_CHANGE, 'token-change')
    .set(RegistryScreen.TOKEN_CHANGE_VERIFICATION, 'token-change/verification')
    .set(RegistryScreen.TOKEN_CHANGE_OTP, 'token-change/otp')
    .set(RegistryScreen.EMAIL_CHANGE_CONFIRMATION, 'email-change/confirmation')
    .set(
      RegistryScreen.ACCOUNT_ALLOCATION_STATUS_CHECK_UPDATE_REQUEST,
      'account/[identifier]/allocation-status/check-update-request'
    )
    .set(
      RegistryScreen.ETS_ADMIN_REQUEST_ALLOCATION,
      'ets-administration/request-allocation'
    )
    .set(
      RegistryScreen.ETS_ADMIN_ALLOCATION_TABLE_CHECK_SUBMIT,
      'ets-administration/allocation-table/check-request-and-submit'
    )
    .set(
      RegistryScreen.CHECK_ANSWERS_AND_SUBMIT,
      'registration/check-answers-and-submit'
    )
    .set(RegistryScreen.CHECK_ANSWERS, 'account-opening/check-answers')
    .set(RegistryScreen.REGISTERED, 'registration/registered')
    .set(RegistryScreen.REGISTER, '')
    .set(RegistryScreen.HOME, 'dashboard')
    .set(RegistryScreen.LANDING_PAGE, '')
    .set(RegistryScreen.EMAIL_INFO, 'registration/emailInfo')
    .set(RegistryScreen.EMAIL_ADDRESS, 'registration/emailAddress')
    .set(RegistryScreen.EMAIL_CONFIRMED, 'registration/emailConfirm')
    .set(RegistryScreen.START_REGISTRATION, '')
    .set(RegistryScreen.KP_ADMIN, 'kpadministration/issuekpunits')
    .set(
      RegistryScreen.KP_ADMIN_ITL_MESSAGES,
      'kpadministration/itl-message-list'
    )
    .set(
      RegistryScreen.KP_ADMIN_ITL_MESSAGES_SPECIFIC_MESSAGE,
      'kpadministration/itl-message-details/[identifier]'
    )
    .set(RegistryScreen.SEARCH_TRANSACTIONS, 'transaction-list')
    .set(RegistryScreen.USER_LIST, 'user-list')
    .set(
      RegistryScreen.FILL_IN_INSTALLATION_DETAILS,
      'account-opening/operator/installation'
    )
    .set(RegistryScreen.REGISTRY_ACTIVATION, 'dashboard/registry-activation')
    .set(
      RegistryScreen.REGISTRY_ACTIVATION_CODE_REQUEST,
      'dashboard/registry-activation/activation-code-request'
    )
    .set(
      RegistryScreen.REGISTRY_ACTIVATION_CODE_REQUEST_SUBMITTED,
      'dashboard/registry-activation/request-submitted'
    )
    .set(
      RegistryScreen.FILL_IN_INSTALLATION_DETAILS_VIEW,
      'account-opening/operator/overview'
    )
    .set(
      RegistryScreen.ACCOUNT_OPENING_REQUEST_NEW_REGISTRY_ACCOUNT,
      'account-opening'
    )
    .set(RegistryScreen.ACCOUNT_LIST, 'account-list')
    .set(RegistryScreen.REGISTRY_DASHBOARD, 'dashboard')
    .set(RegistryScreen.NOTIFICATIONS, 'notifications')
    .set(
      RegistryScreen.NOTIFICATION_REQUEST,
      'notifications/notification-request'
    )
    .set(RegistryScreen.REPORTS_DOWNLOADS, 'reports/downloads')
    .set(RegistryScreen.REPORTS_STANDARD, 'reports/standard')
    .set(RegistryScreen.TASK_LIST, 'task-list')
    .set(RegistryScreen.TASK_LIST_BULK_CLAIM, 'task-list/bulk-claim')
    .set(RegistryScreen.TASK_LIST_BULK_ASSIGN, 'task-list/bulk-assign')
    .set(
      RegistryScreen.REQUEST_A_NEW_ACCOUNT_OVERVIEW,
      'account-opening/task-list'
    )
    .set(RegistryScreen.SPECIFY_THE_ACCESS_RIGHTS, 'does not exist yet')
    .set(RegistryScreen.SPECIFY_THE_REPRESENTATIVE, 'does not exist yet')
    .set(RegistryScreen.VIEW_AUTHORISED_REPRESENTATIVE, 'does not exist yet')
    .set(RegistryScreen.SIGN_IN, AppRoutesPerScreenUtil.NOT_DEFINED_ROUTE)
    .set(RegistryScreen.ACCOUNT_OPENING_SETUP_TWO_AUTH_FACTOR, 'setup-2fa')
    .set(RegistryScreen.TASK_DETAILS, 'task-details/[identifier]')
    .set(
      RegistryScreen.TASK_CONFIRMATION_APPROVE,
      'task-details/[identifier]/approve'
    )
    .set(RegistryScreen.FORGOT_PASSWORD, 'forgot-password')
    .set(RegistryScreen.FORGOT_PASSWORD_RESET, 'forgot-password/reset-password')
    .set(
      RegistryScreen.TASK_CONFIRMATION_APPROVED,
      'task-details/[identifier]/approved'
    )
    .set(
      RegistryScreen.SIGN_IN_2FA_AUTHENTICATION,
      'auth/realms/uk-ets/login-actions/authenticate'
    )
    .set(
      RegistryScreen.TASK_CONFIRMATION_REJECT,
      'task-details/[identifier]/reject'
    )
    .set(
      RegistryScreen.TASK_DETAILS_COMPLETE,
      'task-details/[identifier]/complete'
    )
    .set(
      RegistryScreen.TASK_DETAILS_USER,
      'task-details/[identifier]/user/[identifier]'
    )
    .set(RegistryScreen.TASK_COMMENTS, 'task-details/history/[identifier]')
    .set(
      RegistryScreen.ACCOUNT_HOLDINGS_DETAILS,
      'account/[identifier]/holdings/details'
    )
    .set(
      RegistryScreen.ACCOUNT_TRANSFER,
      'account/[identifier]/account-transfer'
    )
    .set(
      RegistryScreen.ACCOUNT_AUTHORIZED_REPRESENTATIVES,
      'account/[identifier]/authorised-representatives'
    )
    .set(RegistryScreen.VIEW_ACCOUNT, 'account/[identifier]')
    .set(
      RegistryScreen.ADD_TRUSTED_ACCOUNT,
      'account/[identifier]/trusted-account-list/add-account'
    )
    .set(
      RegistryScreen.REMOVE_TRUSTED_ACCOUNT,
      'account/[identifier]/trusted-account-list/remove-account'
    )
    .set(
      RegistryScreen.TRUSTED_ACCOUNT_CHECK_UPDATE_REQUEST,
      'account/[identifier]/trusted-account-list/check-update-request'
    )
    .set(
      RegistryScreen.CHANGE_DESCRIPTION,
      'account/[identifier]/trusted-account-list/change-description'
    )
    .set(
      RegistryScreen.CHECK_DESCRIPTION_ANSWERS,
      'account/[identifier]/trusted-account-list/check-description-answers'
    )
    .set(
      RegistryScreen.VIEW_ACCOUNT_ALLOCATION_STATUS,
      'account/[identifier]/allocation-status'
    )
    .set(
      RegistryScreen.ACCOUNT_OVERVIEW_TAL_TRANSACTION_RULES,
      'account/[identifier]/tal-transaction-rules'
    )
    .set(
      RegistryScreen.ACCOUNT_OVERVIEW_TRANSACTIONS,
      'account/[identifier]/transactions'
    )
    .set(RegistryScreen.CHANGE_ACCOUNT_STATUS_KP, 'account/[identifier]/status')
    .set(
      RegistryScreen.CHANGE_ACCOUNT_STATUS_CONFIRM_KP,
      'account/[identifier]/status/confirm'
    )
    .set(
      RegistryScreen.CHANGE_ACCOUNT_STATUS_ETS,
      'account/[identifier]/status'
    )
    .set(
      RegistryScreen.ACCOUNT_TRUSTED_ACCOUNT_LIST,
      'account/[identifier]/trusted-account-list'
    )
    .set(
      RegistryScreen.CHANGE_ACCOUNT_STATUS_CONFIRM_ETS,
      'account/[identifier]/status/confirm'
    )
    .set(RegistryScreen.EMAIL_VERIFY, 'registration/emailVerify')
    .set(RegistryScreen.TRANSACTION_DETAILS, 'transaction-details/[identifier]')
    .set(RegistryScreen.USER_DETAILS, 'user-details/[identifier]')
    .set(RegistryScreen.USER_MY_PROFILE, 'user-details/my-profile')
    .set(RegistryScreen.CHANGE_USER_STATUS, 'user-details/[identifier]/status')
    .set(
      RegistryScreen.CHANGE_USER_STATUS_CONFIRM,
      'user-details/[identifier]/status/confirm'
    );

  static get(screenName: string): string {
    return AppRoutesPerScreenUtil.routesPerScreenName.get(screenName);
  }
}
