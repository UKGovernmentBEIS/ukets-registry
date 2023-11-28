/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { KnowsThePage } from './knows-the-page.po';
import { KnowsThePersonalDetailsPage } from '../page-objects/user-registration/personal-details.po';
import { RegistryScreen } from './screens';
import { KnowsTheWorkContactDetailsPage } from '../page-objects/user-registration/work-contact-details.po';
import { KnowsTheChoosePasswordPage } from '../page-objects/user-registration/choose-password.po';
import { KnowsTheMemorablePhrasePage } from '../page-objects/user-registration/memorable-phrase.po';
import { KnowsTheCheckAnswersAndSubmitPage } from '../page-objects/user-registration/check-answers-and-submit.po';
import { KnowsTheRegisteredPage } from '../page-objects/user-registration/registered.po';
import { KnowsTheStartRegistrationPage } from '../page-objects/user-registration/start-registration.po';
import { KnowsTheRegisterPage } from '../page-objects/user-registration/register.po';
import { KnowsTheVerifyYourEmailPage } from '../page-objects/user-registration/verify-your-email.po';
import { KnowsTheEmailConfirmedPage } from '../page-objects/user-registration/email-confirmed.po';
import { KnowsTheEmailAddressPage } from '../page-objects/user-registration/email-address.po';
import { KnowsTheFillinInstallationDetailsPage } from '../page-objects/account-opening/fill-in-installation-details.po';
import { KnowsTheFillinInstallationDetailsViewPage } from '../page-objects/account-opening/fill-in-installation-details-view.po';
import { KnowsTheHomePage } from '../page-objects/user-registration/home.po';
import { KnowsTheEmailInfoPage } from '../page-objects/user-registration/email-info.po';
import { KnowsTheAccOpenReqRegAccount } from '../page-objects/account-opening/account-opening-request-open-new-account.po';
import { KnowsTheAccountListPage } from '../page-objects/account-management/account-list.po';
import { KnowsTheLandingPagePage } from '../page-objects/user-registration/landing-page.po';
import { KnowsTheRegistryDashboardPage } from '../page-objects/user-registration/registry-dashboard.po';
import { KnowsTheRequestaNewAccountOverviewPage } from '../page-objects/account-opening/request-a-new-account-overview.po';
import { KnowsTheSigninPage } from '../page-objects/account-opening/sign-in.po';
import { KnowsTheViewAuthorisedRepresentativePage } from '../page-objects/account-opening/view-authorised-representative.po';
import { KnowsTheTaskListPage } from '../page-objects/task-management/task-list.po';
import { KnowsTheTaskListBulkClaimPage } from '../page-objects/task-management/task-list-bulk-claim.po';
import { KnowsTheTaskListBulkAssignPage } from '../page-objects/task-management/task-list-bulk-assign.po';
import { KnowsTheCheckAnswersPage } from '../page-objects/account-opening/check-answers.po';
import { KnowsTheTaskDetailsPage } from '../page-objects/task-management/task-details.po';
import { KnowsTheTaskDetailsCompletePage } from '../page-objects/task-management/task-details-complete.po';
import { KnowsTheTaskCommentsPage } from '../page-objects/task-management/task-comments.po';
import { KnowsTheKpAdminPage } from '../page-objects/kp-admin/kp-admin.po';
import { KnowsTheUserListPage } from '../page-objects/user-validation/user-list.po';
import { KnowsTheTaskConfirmationApprovePage } from '../page-objects/task-management/task-confirmation-approve.po';
import { KnowsTheTaskConfirmationApprovedPage } from '../page-objects/task-management/task-confirmation-approved.po';
import { KnowsTheTaskConfirmationRejectPage } from '../page-objects/task-management/task-confirmation-reject.po';
import { KnowsTheSearchTransactionsPage } from '../page-objects/transactions/search-transactions.po';
import { KnowsTheTransactionDetailsPage } from '../page-objects/transactions/transaction-details.po';
import { KnowsTheChangeAccountStatusKpPage } from '../page-objects/account-management/change-account-status-kp.po';
import { KnowsTheChangeAccountStatusConfirmKpPage } from '../page-objects/account-management/change-account-status-confirm-kp.po';
import { KnowsTheChangeAccountStatusEtsPage } from '../page-objects/account-management/change-account-status-ets.po';
import { KnowsTheChangeAccountStatusConfirmEtsPage } from '../page-objects/account-management/change-account-status-confirm-ets.po';
import { KnowsTheViewAccountOvPha1001TransactPage } from '../page-objects/account-management/account-transactions.po';
import { KnowsTheUserDetailsPage } from '../page-objects/user-validation/user-details.po';
import { KnowsTheAccOverviewTALPage } from '../page-objects/account-management/account-trusted-account-list.po';
import { KnowsTheAccountHoldingsDetailsPage } from '../page-objects/account-management/account-holdings-details.po';
import { KnowsTheUserStatusChangePage } from '../page-objects/user-validation/user-status-change.po';
import { KnowsTheUserStatusChangeConfirmationPage } from '../page-objects/user-validation/user-status-change-confirmation.po';
import { KnowsTheViewAccount50001TalTransactionRulesPage } from '../page-objects/transactions/tal-transaction-rules.po';
import { KnowsTheViewAccount50001AuthRepsPage } from '../page-objects/account-management/account-auth-reps.po';
import { KnowsTheEtsAdministrationProposeIssueUkAllowancesPage } from '../page-objects/ets-transactions/ets-administration-propose-issue-uk-allowances.po';
import { KnowsTheEtsAdministrationAllocationTablePage } from '../page-objects/ets-transactions/ets-administration-allocation-table.po';
import { KnowsTheViewAccountAllocationStatusPage } from '../page-objects/account-management/account-allocation-status.po';
import { KnowsTheEtsAdministrationAllocationTableCheckSubmitPage } from '../page-objects/ets-transactions/ets-administration-allocation-table-check-submit.po';
import { KnowsTheEtsAdministrationProposeIssueUkAllowancesReqAllocPage } from '../page-objects/ets-transactions/ets-administration-request-allocation.po';
import { KnowsTheViewAccountAllocationStatusCheckUpdateRequestPage } from '../page-objects/account-management/account-allocation-status-check-update-request.po';
import { KnowsTheSetupTwoAuthenticationFactorPage } from '../page-objects/user-registration/setup-two-authentication-factor.po';
import { KnowsTheRequestDocumentsPage } from '../page-objects/account-management/request-documents';
import { KnowsTheTaskDetailsUserPage } from '../page-objects/task-management/task-details-user.po';
import { KnowsTheUserMyProfilePage } from '../page-objects/user-registration/my-profile.po';
import { KnowsTheViewActivationCodeRequestPage } from '../page-objects/user-registration/request-activation-code.po';
import { KnowsTheViewActivationCodeRequestSubmittedPage } from '../page-objects/user-registration/request-activation-code-submitted.po';
import { KnowsTheViewRegistryActivationPage } from '../page-objects/user-registration/request-activation.po';
import { KnowsTheAccountHolderDetailsUpdatePage } from '../page-objects/account-management/account-holder-details-update.po';
import { KnowsTheEmailChangePage } from '../page-objects/user-registration/email-change.po';
import { KnowsTheEmailChangeVerificationPage } from '../page-objects/user-registration/email-change-verification.po';
import { KnowsTheEmailChangeConfirmationPage } from '../page-objects/user-registration/email-change-confirmation.po';
import { KnowsTheTokenChangePage } from '../page-objects/user-registration/token-change.po';
import { KnowsTheTokenChangeOtpPage } from '../page-objects/user-registration/token-change-otp.po';
import { KnowsTheTokenChangeVerificationPage } from '../page-objects/user-registration/token-change-verification.po';
import { KnowsTheEmergencyOtpChangePage } from '../page-objects/user-validation/emergency-otp-change';
import { KnowsTheEmergencyOtpChangeSubmittedPage } from '../page-objects/user-validation/emergency-otp-change-submitted';
import { KnowsTheViewAccountOverviewPage } from '../page-objects/account-management/view-account-overview.po';
import { KnowsTheChangeTrustedAccountDescriptionPage } from '../page-objects/account-management/change-trusted-account-description.po';
import { KnowsTheAddTrustedAccountPage } from '../page-objects/account-management/add-trusted-account.po';
import { KnowsTheRemoveTrustedAccountPage } from '../page-objects/account-management/remove-trusted-account.po';
import { KnowsTheTrustedAccountCheckUpdateRequestPage } from '../page-objects/account-management/trusted-account-check-update-request.po';
import { KnowsTheCheckTrustedAccountDescriptionAnswersPage } from '../page-objects/account-management/check-trusted-account-description-answers.po';
import { KnowsTheSignIn2FaAuthenticationPage } from '../page-objects/user-validation/sign-in-2fa-authentication';
import { KnowsTheForgotPasswordPage } from '../page-objects/user-validation/forgot-password.po';
import { KnowsTheForgotPasswordResetPage } from '../page-objects/user-validation/forgot-password-reset.po';
import { KnowsTheEmergencyPasswordAndOtpChangePage } from '../page-objects/user-validation/emergency-password-and-otp-change';
import { KnowsTheEmergencyAccessRequestSubmittedPage } from '../page-objects/user-validation/emergency-access-request-submitted';
import { KnowsTheResetYourCredentialsPage } from '../page-objects/user-validation/reset-your-credentials';
import { KnowsTheEnterYourNewPasswordPage } from '../page-objects/user-validation/enter-your-new-password';
import { KnowsTheSuccessfullyChangedCredentialsPage } from '../page-objects/user-validation/changed-credentials-successfully';
import { KnowsTheChangePasswordPage } from '../page-objects/user-validation/change-password.po';
import { KnowsTheChangePasswordConfirmationPage } from '../page-objects/user-validation/change-password-confirmation.po';
import { KnowsTheKpAdminItlMessagesPage } from '../page-objects/kp-admin/kp-admin-itl-messages.po';
import { KnowsTheKpAdminItlMessagesSpecificMessagePage } from '../page-objects/kp-admin/kp-admin-specific-itl-message.po';
import { KnowsTheKpAdminItlNotificationsPage } from '../page-objects/kp-admin/kp-admin-itl-notices.po';
import { KnowsTheKpAdminItlSpecificNotificationPage } from '../page-objects/kp-admin/kp-admin-itl-specific-notice.po';
import { KnowsTheKpAdminItlSpecificNotificationTypeDetailsPage } from '../page-objects/kp-admin/kp-admin-itl-specific-notice-type-details.po';
import { KnowsTheAccountOperatorUpdatePage } from '../page-objects/account-management/account-operator-update.po';
import { KnowsTheReportsDownloadsPage } from '../page-objects/reports/reports-downloads.po';
import { KnowsTheReportsStandardPage } from '../page-objects/reports/reports-standard.po';
import { KnowsTheAccountTransferPage } from '../page-objects/account-management/account-transfer.po';
import { KnowsTheSpecifytheAccessRightsPage } from '../page-objects/account-opening/specify-the-access-rights.po';
import { KnowsTheSpecifytheRepresentativePage } from '../page-objects/account-opening/specify-the-representative.po';
import { KnowsThePublicReportsKpPage } from '../page-objects/reports/public-reports-kp.po';
import { KnowsThePublicReportsEtsPage } from '../page-objects/reports/public-reports-ets.po';
import { KnowsEtsTheUploadEmissionsTablePage } from '../page-objects/compliance/compliance-upload-emissions-table.po';
import { KnowsEtsTheUploadEmissionsTableCheckPage } from '../page-objects/compliance/compliance-upload-emissions-table-check.po';
import { KnowsTheAccountClosurePage } from '../page-objects/account-management/account-closure.po';
import { KnowsTheUpdateExclusionStatusPage } from '../page-objects/compliance/update_exclusion_status.po';
import { KnowsTheNotificationsPage } from '../page-objects/user-registration/notifications.po';
import { KnowsTheNotificationRequestPage } from '../page-objects/user-registration/notification-request.po';

export class PageObjectPerScreenFactory {
  private static poPerScreenMap: Map<string, KnowsThePage>;

  static initAllPageObjects() {
    PageObjectPerScreenFactory.poPerScreenMap = new Map<string, KnowsThePage>()
      .set(
        RegistryScreen.KYOTO_PROTOCOL_PUBLIC_REPORTS,
        new KnowsThePublicReportsKpPage()
      )
      .set(RegistryScreen.ACCOUNT_CLOSURE, new KnowsTheAccountClosurePage())
      .set(
        RegistryScreen.UPDATE_EXCLUSION_STATUS,
        new KnowsTheUpdateExclusionStatusPage()
      )
      .set(
        RegistryScreen.ETS_ADMIN_UPLOAD_EMISSIONS_TABLE_CHECK,
        new KnowsEtsTheUploadEmissionsTableCheckPage()
      )
      .set(
        RegistryScreen.ETS_ADMIN_UPLOAD_EMISSIONS_TABLE,
        new KnowsEtsTheUploadEmissionsTablePage()
      )
      .set(
        RegistryScreen.ETS_PUBLIC_REPORTS,
        new KnowsThePublicReportsEtsPage()
      )
      .set(
        RegistryScreen.SUCCESSFULLY_CHANGED_YOUR_CREDENTIALS,
        new KnowsTheSuccessfullyChangedCredentialsPage()
      )
      .set(RegistryScreen.PERSONAL_DETAILS, new KnowsThePersonalDetailsPage())
      .set(
        RegistryScreen.VIEW_ACCOUNT_OPERATOR_UPDATE,
        new KnowsTheAccountOperatorUpdatePage()
      )
      .set(RegistryScreen.CHANGE_PASSWORD, new KnowsTheChangePasswordPage())
      .set(RegistryScreen.ACCOUNT_TRANSFER, new KnowsTheAccountTransferPage())
      .set(
        RegistryScreen.KP_ADMIN_ITL_MESSAGES,
        new KnowsTheKpAdminItlMessagesPage()
      )
      .set(RegistryScreen.REPORTS_DOWNLOADS, new KnowsTheReportsDownloadsPage())
      .set(RegistryScreen.REPORTS_STANDARD, new KnowsTheReportsStandardPage())
      .set(
        RegistryScreen.KP_ADMIN_ITL_MESSAGES_SPECIFIC_MESSAGE,
        new KnowsTheKpAdminItlMessagesSpecificMessagePage()
      )
      .set(
        RegistryScreen.CHANGE_PASSWORD_CONFIRMATION,
        new KnowsTheChangePasswordConfirmationPage()
      )

      .set(RegistryScreen.TASK_DETAILS_USER, new KnowsTheTaskDetailsUserPage())
      .set(
        RegistryScreen.ENTER_YOUR_NEW_PASSWORD,
        new KnowsTheEnterYourNewPasswordPage()
      )
      .set(
        RegistryScreen.KP_ADMIN_ITL_NOTIFICATIONS,
        new KnowsTheKpAdminItlNotificationsPage()
      )
      .set(
        RegistryScreen.KP_ADMIN_ITL_SPECIFIC_NOTIFICATION,
        new KnowsTheKpAdminItlSpecificNotificationPage()
      )
      .set(
        RegistryScreen.KP_ADMIN_ITL_SPECIFIC_NOTIFICATION_TYPE_DETAILS,
        new KnowsTheKpAdminItlSpecificNotificationTypeDetailsPage()
      )
      .set(
        RegistryScreen.RESET_YOUR_CREDENTIALS,
        new KnowsTheResetYourCredentialsPage()
      )
      .set(
        RegistryScreen.EMERGENCY_ACCESS_REQUEST_SUBMITTED,
        new KnowsTheEmergencyAccessRequestSubmittedPage()
      )
      .set(
        RegistryScreen.SIGN_IN_2FA_AUTHENTICATION,
        new KnowsTheSignIn2FaAuthenticationPage()
      )
      .set(RegistryScreen.FORGOT_PASSWORD, new KnowsTheForgotPasswordPage())
      .set(
        RegistryScreen.FORGOT_PASSWORD_RESET,
        new KnowsTheForgotPasswordResetPage()
      )
      .set(RegistryScreen.VIEW_ACCOUNT, new KnowsTheViewAccountOverviewPage())
      .set(
        RegistryScreen.ADD_TRUSTED_ACCOUNT,
        new KnowsTheAddTrustedAccountPage()
      )
      .set(
        RegistryScreen.REMOVE_TRUSTED_ACCOUNT,
        new KnowsTheRemoveTrustedAccountPage()
      )
      .set(
        RegistryScreen.TRUSTED_ACCOUNT_CHECK_UPDATE_REQUEST,
        new KnowsTheTrustedAccountCheckUpdateRequestPage()
      )
      .set(
        RegistryScreen.CHANGE_DESCRIPTION,
        new KnowsTheChangeTrustedAccountDescriptionPage()
      )
      .set(
        RegistryScreen.CHECK_DESCRIPTION_ANSWERS,
        new KnowsTheCheckTrustedAccountDescriptionAnswersPage()
      )
      .set(
        RegistryScreen.EMERGENCY_PASSWORD_AND_OTP_CHANGE,
        new KnowsTheEmergencyPasswordAndOtpChangePage()
      )
      .set(RegistryScreen.EMAIL_CHANGE, new KnowsTheEmailChangePage())
      .set(RegistryScreen.TOKEN_CHANGE, new KnowsTheTokenChangePage())
      .set(
        RegistryScreen.TOKEN_CHANGE_VERIFICATION,
        new KnowsTheTokenChangeVerificationPage()
      )
      .set(RegistryScreen.TOKEN_CHANGE_OTP, new KnowsTheTokenChangeOtpPage())
      .set(
        RegistryScreen.EMAIL_CHANGE_VERIFICATION,
        new KnowsTheEmailChangeVerificationPage()
      )
      .set(
        RegistryScreen.EMAIL_CHANGE_CONFIRMATION,
        new KnowsTheEmailChangeConfirmationPage()
      )
      .set(RegistryScreen.USER_MY_PROFILE, new KnowsTheUserMyProfilePage())
      .set(
        RegistryScreen.WORK_CONTACT_DETAILS,
        new KnowsTheWorkContactDetailsPage()
      )
      .set(
        RegistryScreen.EMERGENCY_OTP_CHANGE,
        new KnowsTheEmergencyOtpChangePage()
      )
      .set(
        RegistryScreen.EMERGENCY_OTP_CHANGE_SUBMITTED,
        new KnowsTheEmergencyOtpChangeSubmittedPage()
      )
      .set(RegistryScreen.MEMORABLE_PHRASE, new KnowsTheMemorablePhrasePage())
      .set(RegistryScreen.CHOOSE_PWD, new KnowsTheChoosePasswordPage())
      .set(
        RegistryScreen.CHECK_ANSWERS_AND_SUBMIT,
        new KnowsTheCheckAnswersAndSubmitPage()
      )
      .set(RegistryScreen.REQUEST_DOCUMENTS, new KnowsTheRequestDocumentsPage())
      .set(
        RegistryScreen.ACCOUNT_HOLDINGS_DETAILS,
        new KnowsTheAccountHoldingsDetailsPage()
      )
      .set(
        RegistryScreen.ACCOUNT_OPENING_SETUP_TWO_AUTH_FACTOR,
        new KnowsTheSetupTwoAuthenticationFactorPage()
      )
      .set(RegistryScreen.CHECK_ANSWERS, new KnowsTheCheckAnswersPage())
      .set(RegistryScreen.REGISTERED, new KnowsTheRegisteredPage())
      .set(RegistryScreen.REGISTER, new KnowsTheRegisterPage())
      .set(RegistryScreen.EMAIL_VERIFY, new KnowsTheVerifyYourEmailPage())
      .set(RegistryScreen.EMAIL_CONFIRMED, new KnowsTheEmailConfirmedPage())
      .set(RegistryScreen.EMAIL_ADDRESS, new KnowsTheEmailAddressPage())
      .set(
        RegistryScreen.ETS_ADMIN_PROPOSE_ISSUE_UK_ALLOWANCES,
        new KnowsTheEtsAdministrationProposeIssueUkAllowancesPage()
      )
      .set(
        RegistryScreen.ETS_ADMIN_REQUEST_ALLOCATION,
        new KnowsTheEtsAdministrationProposeIssueUkAllowancesReqAllocPage()
      )
      .set(
        RegistryScreen.ETS_ADMIN_ALLOCATION_TABLE_CHECK_SUBMIT,
        new KnowsTheEtsAdministrationAllocationTableCheckSubmitPage()
      )
      .set(
        RegistryScreen.ETS_ADMIN_ALLOCATION_TABLE,
        new KnowsTheEtsAdministrationAllocationTablePage()
      )
      .set(
        RegistryScreen.START_REGISTRATION,
        new KnowsTheStartRegistrationPage()
      )
      .set(
        RegistryScreen.ACCOUNT_ALLOCATION_STATUS_CHECK_UPDATE_REQUEST,
        new KnowsTheViewAccountAllocationStatusCheckUpdateRequestPage()
      )
      .set(
        RegistryScreen.CHANGE_ACCOUNT_STATUS_KP,
        new KnowsTheChangeAccountStatusKpPage()
      )
      .set(
        RegistryScreen.CHANGE_ACCOUNT_STATUS_CONFIRM_KP,
        new KnowsTheChangeAccountStatusConfirmKpPage()
      )
      .set(
        RegistryScreen.ACCOUNT_HOLDER_DETAILS_UPDATE,
        new KnowsTheAccountHolderDetailsUpdatePage()
      )
      .set(
        RegistryScreen.CHANGE_ACCOUNT_STATUS_ETS,
        new KnowsTheChangeAccountStatusEtsPage()
      )
      .set(
        RegistryScreen.CHANGE_ACCOUNT_STATUS_CONFIRM_ETS,
        new KnowsTheChangeAccountStatusConfirmEtsPage()
      )
      .set(
        RegistryScreen.VIEW_ACCOUNT_ALLOCATION_STATUS,
        new KnowsTheViewAccountAllocationStatusPage()
      )
      .set(
        RegistryScreen.REGISTRY_ACTIVATION_CODE_REQUEST,
        new KnowsTheViewActivationCodeRequestPage()
      )
      .set(
        RegistryScreen.REGISTRY_ACTIVATION,
        new KnowsTheViewRegistryActivationPage()
      )
      .set(
        RegistryScreen.REGISTRY_ACTIVATION_CODE_REQUEST_SUBMITTED,
        new KnowsTheViewActivationCodeRequestSubmittedPage()
      )
      .set(
        RegistryScreen.ACCOUNT_OVERVIEW_TRANSACTIONS,
        new KnowsTheViewAccountOvPha1001TransactPage()
      )
      .set(
        RegistryScreen.ACCOUNT_OVERVIEW_TAL_TRANSACTION_RULES,
        new KnowsTheViewAccount50001TalTransactionRulesPage()
      )
      .set(
        RegistryScreen.ACCOUNT_AUTHORIZED_REPRESENTATIVES,
        new KnowsTheViewAccount50001AuthRepsPage()
      )
      .set(RegistryScreen.KP_ADMIN, new KnowsTheKpAdminPage())
      .set(
        RegistryScreen.SEARCH_TRANSACTIONS,
        new KnowsTheSearchTransactionsPage()
      )
      .set(
        RegistryScreen.TRANSACTION_DETAILS,
        new KnowsTheTransactionDetailsPage()
      )
      .set(RegistryScreen.TASK_COMMENTS, new KnowsTheTaskCommentsPage())
      .set(RegistryScreen.TASK_DETAILS, new KnowsTheTaskDetailsPage())
      .set(
        RegistryScreen.TASK_CONFIRMATION_APPROVE,
        new KnowsTheTaskConfirmationApprovePage()
      )
      .set(
        RegistryScreen.TASK_CONFIRMATION_APPROVED,
        new KnowsTheTaskConfirmationApprovedPage()
      )
      .set(
        RegistryScreen.TASK_CONFIRMATION_REJECT,
        new KnowsTheTaskConfirmationRejectPage()
      )
      .set(
        RegistryScreen.TASK_DETAILS_COMPLETE,
        new KnowsTheTaskDetailsCompletePage()
      )
      .set(RegistryScreen.USER_LIST, new KnowsTheUserListPage())
      .set(RegistryScreen.USER_DETAILS, new KnowsTheUserDetailsPage())
      .set(
        RegistryScreen.CHANGE_USER_STATUS,
        new KnowsTheUserStatusChangePage()
      )
      .set(
        RegistryScreen.CHANGE_USER_STATUS_CONFIRM,
        new KnowsTheUserStatusChangeConfirmationPage()
      )
      .set(
        RegistryScreen.FILL_IN_INSTALLATION_DETAILS,
        new KnowsTheFillinInstallationDetailsPage()
      )
      .set(
        RegistryScreen.FILL_IN_INSTALLATION_DETAILS_VIEW,
        new KnowsTheFillinInstallationDetailsViewPage()
      )
      .set(RegistryScreen.HOME, new KnowsTheHomePage())
      .set(RegistryScreen.NOTIFICATIONS, new KnowsTheNotificationsPage())
      .set(
        RegistryScreen.NOTIFICATION_REQUEST,
        new KnowsTheNotificationRequestPage()
      )
      .set(RegistryScreen.LANDING_PAGE, new KnowsTheLandingPagePage())
      .set(
        RegistryScreen.REGISTRY_DASHBOARD,
        new KnowsTheRegistryDashboardPage()
      )
      .set(
        RegistryScreen.REQUEST_A_NEW_ACCOUNT_OVERVIEW,
        new KnowsTheRequestaNewAccountOverviewPage()
      )
      .set(RegistryScreen.EMAIL_INFO, new KnowsTheEmailInfoPage())
      .set(RegistryScreen.SIGN_IN, new KnowsTheSigninPage())
      .set(
        RegistryScreen.ACCOUNT_OPENING_REQUEST_NEW_REGISTRY_ACCOUNT,
        new KnowsTheAccOpenReqRegAccount()
      )
      .set(RegistryScreen.ACCOUNT_LIST, new KnowsTheAccountListPage())
      .set(RegistryScreen.TASK_LIST, new KnowsTheTaskListPage())
      .set(
        RegistryScreen.ACCOUNT_TRUSTED_ACCOUNT_LIST,
        new KnowsTheAccOverviewTALPage()
      )
      .set(
        RegistryScreen.TASK_LIST_BULK_CLAIM,
        new KnowsTheTaskListBulkClaimPage()
      )
      .set(
        RegistryScreen.TASK_LIST_BULK_ASSIGN,
        new KnowsTheTaskListBulkAssignPage()
      )
      .set(
        RegistryScreen.SPECIFY_THE_ACCESS_RIGHTS,
        new KnowsTheSpecifytheAccessRightsPage()
      )
      .set(
        RegistryScreen.SPECIFY_THE_REPRESENTATIVE,
        new KnowsTheSpecifytheRepresentativePage()
      )
      .set(
        RegistryScreen.VIEW_AUTHORISED_REPRESENTATIVE,
        new KnowsTheViewAuthorisedRepresentativePage()
      );
  }

  static getFromScreen(screenName: string): KnowsThePage {
    return this.poPerScreenMap.get(screenName);
  }
}
