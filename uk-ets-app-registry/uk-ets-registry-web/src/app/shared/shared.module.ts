import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import {
  ErrorSummaryComponent,
  ErrorSummaryContainerComponent,
} from '@shared/error-summary';
import { BackButtonComponent } from './back-button/back-button.component';
import { ConnectFormDirective } from './connect-form.directive';
import { UkTextInputComponent } from './form-controls/uk-text-input/uk-text-input.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkSelectInputComponent } from './form-controls/uk-select-input/uk-select-input.component';
import { DebounceClickDirective } from './debounce-click.directive';
import { BannerComponent } from './banner/banner.component';
import { RouterModule } from '@angular/router';
import { TypeAheadComponent } from './form-controls/type-ahead/type-ahead.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SortableColumnDirective } from './search/sort/sortable-column.directive';
import { SortableTableDirective } from './search/sort/sortable-table.directive';
import { RouterLinkDirectiveStub } from './test/router-link-directive-stub';
import { BackToTopComponent } from './back-to-top/back-to-top.component';
import {
  UkProtoFormCommentAreaComponent,
  UkProtoFormDateComponent,
  UkProtoFormDatePickerComponent,
  UkProtoFormEmailComponent,
  UkProtoFormSelectComponent,
  UkProtoFormTextareaComponent,
  UkProtoFormTextComponent,
  UkProtoFormTypeAheadComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import {
  UkDateControlComponent,
  UkRichTextEditorComponent,
  UkSelectAuthorisedRepresentativeComponent,
  UkSelectFileComponent,
  UKSelectPhoneComponent,
} from '@shared/form-controls';
import {
  AccessRightsPipe,
  AccountAccessPipe,
  AircraftOperatorPipe,
  ApiEnumTypesPipe,
  AllocationCategoryPipe,
  ArDisplayNamePipe,
  ArraySumPipe,
  AuthorisedRepresentativeUpdateTypePipe,
  ConcatDateTimePipe,
  CountryNamePipe,
  CountryNameAsyncPipe,
  DateOfBirthPipe,
  EnvironmentalActivityPipe,
  EventTypePipe,
  FormatUkDatePipe,
  GdsDatePipe,
  GdsDateShortNoYearPipe,
  GdsDateShortPipe,
  GdsDateTimePipe,
  GdsDateTimeShortPipe,
  GdsTimePipe,
  GdsTimeUTCPipe,
  GdsDateTimeUTCPipe,
  GovernmentPipe,
  IndividualFirstAndMiddleNamesPipe,
  IndividualFullNamePipe,
  IndividualPipe,
  InstallationPipe,
  IsBillablePipe,
  KeycloakUserDisplayNamePipe,
  OrganisationPipe,
  ProtectPipe,
  TaskTypeBeforeApprovalLabelPipe,
  TransactionAtrributesPipe,
  TrustedAccountPipe,
  UnitTypeSopRenderPipe,
} from '@shared/pipes';
import { PaginatorComponent } from '@shared/search/paginator';
import { ToggleButtonComponent } from '@shared/search/toggle-button/toggle-button.component';
import { EmptyPageComponent } from './empty-page/empty-page.component';
import { SharedAccountHolderComponent } from './components/account/account-holder/shared-account-holder.component';
import { AccountDetailsViewComponent } from './components/account/account-details/account-details-view.component';
import { InstallationComponent } from './components/account/operator/installation/installation.component';
import { AircraftOperatorComponent } from './components/account/operator/aircraft-operator/aircraft-operator.component';
import {
  ArUpdateAccessRightsComponent,
  ArUpdateTypeComponent,
  ArUpdateUserComponent,
  AuthRepContactComponent,
  AuthRepTableComponent,
} from '@shared/components/account/authorised-representatives';
import { ThreeLineAddressComponent } from './components/three-line-address/three-line-address.component';
import { PhoneNumberComponent } from './components/phone-number/phone-number.component';
import {
  IssuanceTransactionSummaryTableComponent,
  ItlNotificationSummaryComponent,
  TransactionConnectionSummaryComponent,
  TransactionSigningDetailsComponent,
} from '@shared/components/transactions';
import { UkRadioInputComponent } from './form-controls/uk-radio-input/uk-radio-input.component';
import { QuantityTableComponent } from './components/transactions/quantity-table';
import { NatAndNerAllocationDetailsTableComponent } from '@shared/components/transactions/nat-and-ner-allocation-details-table/nat-and-ner-allocation-details-table.component';
import { AccountSummaryComponent } from './components/transactions/account-summary/account-summary.component';
import { UkActivationCodeInputComponent } from '@shared/form-controls/uk-activation-code-input/uk-activation-code-input.component';
import { AccountHolderContactComponent } from '@shared/components/account/account-holder-contact/account-holder-contact.component';
import { SkipLinkComponent } from '@shared/components/skip-link-component/skip-link.component';
import { SideMenuComponent } from '@shared/side-menu/side-menu.component';
import { TrustedAccountTableComponent } from '@shared/components/account/trusted-account-table';
import { AccountInputComponent } from '@shared/form-controls/account-input/account-input.component';
import { AccountAccessService } from '../auth/account-access.service';
import { CancelUpdateRequestComponent } from '@shared/components/cancel-update-request';
import { RequestSubmittedComponent } from '@shared/components/account/request-submitted';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { DomainEventsComponent } from './components/event/domain-events/domain-events.component';
import { AllowanceQuantityTableComponent } from '@shared/components/transactions/allowance-quantity-table';
import { UkProtoFormComponent } from '@shared/form-controls/uk-proto-form.component';
import { RulesForAuthorisedRepresentativeComponent } from '@shared/components/account/rules-for-authorised-representative';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { IdentificationDocumentationListComponent } from '@shared/components/identification-documentation-list';
import { RequestDocPipe } from '@shared/pipes/request-doc.pipe';
import { UkSingleCheckboxComponent } from './form-controls/uk-single-checkbox/uk-single-checkbox.component';
import { AccountHolderSummaryChangesComponent } from '@shared/components/account/account-holder-summary-changes';
import { AccountHolderIndividualDetailsComponent } from '@shared/components/account/account-holder-individual-details';
import { AccountHolderOrganisationDetailsComponent } from '@shared/components/account/account-holder-organisation-details';
import { AccountHolderOrganisationAddressComponent } from '@shared/components/account/account-holder-organisation-address';
import { AccountHolderIndividualContactDetailsComponent } from '@shared/components/account/account-holder-individual-contact-details';
import { MyProfileComponent } from './header/my-profile.component';
import { AccountHolderContactDetailsComponent } from '@shared/components/account/account-holder-contact-details';
import { SubWizardTitleComponent } from '@shared/components/account/sub-wizard-title';
import { AccountHolderContactWorkDetailsComponent } from '@shared/components/account/account-holder-contact-work-details';
import { SummaryListComponent } from '@shared/summary-list';
import { SafeUrlPipe } from '@shared/pipes/safe-url.pipe';
import {
  ApiErrorHandlingService,
  TransactionApiService,
  TransactionWizardApiService,
  TypeAheadService,
} from '@shared/services';
import { FeatureHeaderWrapperComponent } from './sub-headers/header-wrapper/feature-header-wrapper.component';
import { PortalModule } from '@angular/cdk/portal';
import {
  CookiesPopUpComponent,
  CookiesPopUpContainerComponent,
} from '@shared/cookies-pop-up';
import { GovukTimePipe } from './pipes/govuk-time.pipe';
import {
  GovukNotificationBannerComponent,
  GovukTagComponent,
} from '@shared/govuk-components';
import { NavMenuComponent } from './header/nav-menu.component';
import { SubmenuContainerComponent } from './sub-menu/submenu-container.component';
import { SubMenuComponent } from '@shared/sub-menu/sub-menu.component';
import { AboutTrustedAccountListComponent } from './components/account/about-trusted-account-list/about-trusted-account-list.component';
// eslint-disable-next-line max-len
import { SearchTransactionsResultsComponent } from './components/transactions/search-transactions-results/search-transactions-results.component';
import { ReconciliationStartComponent } from '@shared/components/reconciliation/reconciliation-start.component';
import { DisableControlDirective } from './form-controls/disable-control.directive';
import { AccountDetailsFormComponent } from '@shared/components/account/account-details/account-details-form.component';
import { PasswordRevealDirective } from './directives/password-reveal.directive';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ExportFileService } from '@shared/export-file/export-file.service';
import {
  ReportSuccessBannerComponent,
  SearchReportButtonComponent,
} from '@shared/components/reports';
import { InstallationInputComponent } from '@shared/components/account/operator/installation-input/installation-input.component';
import { AircraftInputComponent } from '@shared/components/account/operator/aircraft-input';
import { OperatorSummaryChangesComponent } from '@shared/components/account/operator/operator-summary-changes';
import { SpinnerComponent } from '@shared/components/spinner';
import { PersonalDetailsComponent } from '@shared/components/user/personal-details';
import { WorkDetailsComponent } from '@shared/components/user/work-details';
import { PersonalDetailsSummaryChangesComponent } from '@shared/components/user/personal-details-summary-changes';
import { WorkDetailsSummaryChangesComponent } from '@shared/components/user/work-details-summary-changes';
import { UserDeactivationSummaryComponent } from '@shared/components/user/user-deactivation-summary';
import { AccountDetailsSummaryComponent } from '@shared/components/account/account-details-summary';
import { SubmitSuccessChangeDescriptionComponent } from '@registry-web/account-management/account/trusted-account-list/components';
import { MemorablePhraseComponent } from '@shared/components/user/memorable-phrase';
import { MemorablePhraseSummaryChangesComponent } from '@shared/components/user/memorable-phrase-summary-changes';
import { QuillModule } from 'ngx-quill';
import { NotificationHeaderComponent } from '@shared/components/notifications/notification-header';
import { NotificationApiService } from '@shared/components/notifications/services';
import { AuthorisedRepresentativeDetailsPipe } from './pipes/authorised-representative-details.pipe';
import { NotificationRendererComponent } from '@shared/components/notifications/notification-renderer';
import { UkProtoFormDateSelectComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-multi-select/uk-proto-form-date-select.component';
import {
  TransactionReferenceComponent,
  TransactionReferenceWarningComponent,
} from '@shared/components/transactions/transaction-reference';
import { SetTransactionReferenceComponent } from '@shared/components/transactions/set-transaction-reference';
import { AccountClosureWarningsComponent } from '@shared/components/account/account-closure-warnings';
import { CheckAllocationDetailsComponent } from '@transaction-proposal/components/check-allocation-details';
import { EmptySearchResultsComponent } from './search/empty-search-results/empty-search-results.component';
import { BillingDetailsViewComponent } from './components/account/billing-details/billing-details-view.component';
import { BillingDetailsFormComponent } from './components/account/billing-details/billing-details-form.component';
import { ExcludeBillingFormComponent } from './components/account/billing-details/exclude-billing-form.component';
import { ReadMoreComponent } from './components/read-more/read-more.component';
import { NotesListComponent } from './components/notes-list/notes-list.component';
import { NotesApiService } from '@registry-web/notes/services/notes-api.service';

@NgModule({
  declarations: [
    PaginatorComponent,
    ErrorSummaryComponent,
    ErrorSummaryContainerComponent,
    BackButtonComponent,
    ConnectFormDirective,
    UkTextInputComponent,
    UkActivationCodeInputComponent,
    AccountInputComponent,
    UkSelectInputComponent,
    DebounceClickDirective,
    BannerComponent,
    TypeAheadComponent,
    SortableColumnDirective,
    SortableTableDirective,
    RouterLinkDirectiveStub,
    BackToTopComponent,
    UkProtoFormTextComponent,
    UkProtoFormTextareaComponent,
    UKSelectPhoneComponent,
    UkProtoFormEmailComponent,
    UkProtoFormDateComponent,
    UkProtoFormDatePickerComponent,
    UkProtoFormSelectComponent,
    UkProtoFormDateSelectComponent,
    UkProtoFormCommentAreaComponent,
    UkProtoFormTypeAheadComponent,
    ProtectPipe,
    ToggleButtonComponent,
    UkDateControlComponent,
    FormatUkDatePipe,
    DateOfBirthPipe,
    InstallationPipe,
    AircraftOperatorPipe,
    IndividualPipe,
    GovernmentPipe,
    IndividualFullNamePipe,
    IndividualFirstAndMiddleNamesPipe,
    TransactionAtrributesPipe,
    TaskTypeBeforeApprovalLabelPipe,
    OrganisationPipe,
    EmptyPageComponent,
    SharedAccountHolderComponent,
    IsBillablePipe,
    EnvironmentalActivityPipe,
    EventTypePipe,
    AccessRightsPipe,
    AccountHolderContactComponent,
    AccountDetailsViewComponent,
    BillingDetailsViewComponent,
    AccountDetailsFormComponent,
    BillingDetailsFormComponent,
    ExcludeBillingFormComponent,
    InstallationComponent,
    AircraftOperatorComponent,
    AuthRepTableComponent,
    AuthRepContactComponent,
    ThreeLineAddressComponent,
    PhoneNumberComponent,
    AccountAccessPipe,
    IssuanceTransactionSummaryTableComponent,
    ItlNotificationSummaryComponent,
    UkRadioInputComponent,
    QuantityTableComponent,
    NatAndNerAllocationDetailsTableComponent,
    CheckAllocationDetailsComponent,
    SetTransactionReferenceComponent,
    TransactionReferenceComponent,
    TransactionReferenceWarningComponent,
    AccountSummaryComponent,
    SkipLinkComponent,
    TrustedAccountPipe,
    SideMenuComponent,
    TrustedAccountTableComponent,
    CancelUpdateRequestComponent,
    RequestSubmittedComponent,
    UnitTypeSopRenderPipe,
    CancelRequestLinkComponent,
    UkSelectAuthorisedRepresentativeComponent,
    ArUpdateUserComponent,
    ArUpdateAccessRightsComponent,
    ArUpdateTypeComponent,
    AuthorisedRepresentativeUpdateTypePipe,
    GdsDateTimeShortPipe,
    GdsTimePipe,
    GdsTimeUTCPipe,
    GdsDateTimePipe,
    GdsDateShortPipe,
    GdsDateShortNoYearPipe,
    GdsDatePipe,
    GdsDateTimeUTCPipe,
    DomainEventsComponent,
    ArraySumPipe,
    UkProtoFormComponent,
    AllowanceQuantityTableComponent,
    ApiEnumTypesPipe,
    AllocationCategoryPipe,
    UkSelectFileComponent,
    SearchTransactionsResultsComponent,
    AboutTrustedAccountListComponent,
    RulesForAuthorisedRepresentativeComponent,
    IdentificationDocumentationListComponent,
    RequestDocPipe,
    SafeUrlPipe,
    UkSingleCheckboxComponent,
    TransactionSigningDetailsComponent,
    AccountHolderSummaryChangesComponent,
    UkSingleCheckboxComponent,
    AccountHolderIndividualDetailsComponent,
    AccountHolderOrganisationDetailsComponent,
    AccountHolderOrganisationAddressComponent,
    AccountHolderIndividualContactDetailsComponent,
    AccountHolderSummaryChangesComponent,
    AccountHolderContactDetailsComponent,
    AccountHolderContactWorkDetailsComponent,
    SubWizardTitleComponent,
    MyProfileComponent,
    SummaryListComponent,
    FeatureHeaderWrapperComponent,
    CookiesPopUpComponent,
    CookiesPopUpContainerComponent,
    GovukTimePipe,
    ConcatDateTimePipe,
    GovukTagComponent,
    NavMenuComponent,
    SubmenuContainerComponent,
    SubMenuComponent,
    GovukNotificationBannerComponent,
    ReconciliationStartComponent,
    DisableControlDirective,
    PasswordRevealDirective,
    ScreenReaderPageAnnounceDirective,
    SearchReportButtonComponent,
    ReportSuccessBannerComponent,
    InstallationInputComponent,
    AircraftInputComponent,
    OperatorSummaryChangesComponent,
    SpinnerComponent,
    PersonalDetailsComponent,
    WorkDetailsComponent,
    MemorablePhraseComponent,
    PersonalDetailsSummaryChangesComponent,
    UserDeactivationSummaryComponent,
    WorkDetailsSummaryChangesComponent,
    MemorablePhraseSummaryChangesComponent,
    CountryNamePipe,
    CountryNameAsyncPipe,
    AccountDetailsSummaryComponent,
    SubmitSuccessChangeDescriptionComponent,
    TransactionConnectionSummaryComponent,
    UkRichTextEditorComponent,
    NotificationHeaderComponent,
    ArDisplayNamePipe,
    KeycloakUserDisplayNamePipe,
    AuthorisedRepresentativeDetailsPipe,
    NotificationRendererComponent,
    AccountClosureWarningsComponent,
    EmptySearchResultsComponent,
    ReadMoreComponent,
    NotesListComponent,
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    RouterModule,
    NgbModule,
    NgxPageScrollCoreModule,
    PortalModule,
    QuillModule,
  ],
  exports: [
    ToggleButtonComponent,
    ErrorSummaryComponent,
    BackButtonComponent,
    ConnectFormDirective,
    UkTextInputComponent,
    UkActivationCodeInputComponent,
    AccountInputComponent,
    UkSelectInputComponent,
    DebounceClickDirective,
    BannerComponent,
    TypeAheadComponent,
    PaginatorComponent,
    SortableTableDirective,
    SortableColumnDirective,
    RouterLinkDirectiveStub,
    BackToTopComponent,
    UKSelectPhoneComponent,
    UkProtoFormEmailComponent,
    UkProtoFormTextComponent,
    UkProtoFormTextareaComponent,
    UkProtoFormDateComponent,
    UkProtoFormDatePickerComponent,
    UkProtoFormSelectComponent,
    UkProtoFormDateSelectComponent,
    UkProtoFormCommentAreaComponent,
    UkProtoFormTypeAheadComponent,
    ProtectPipe,
    UkDateControlComponent,
    FormatUkDatePipe,
    DateOfBirthPipe,
    InstallationPipe,
    TaskTypeBeforeApprovalLabelPipe,
    AircraftOperatorPipe,
    IndividualPipe,
    GovernmentPipe,
    OrganisationPipe,
    EmptyPageComponent,
    SharedAccountHolderComponent,
    IsBillablePipe,
    AccountHolderContactComponent,
    AccountDetailsViewComponent,
    BillingDetailsViewComponent,
    AccountDetailsFormComponent,
    BillingDetailsFormComponent,
    ExcludeBillingFormComponent,
    AircraftOperatorComponent,
    InstallationComponent,
    AuthRepTableComponent,
    PhoneNumberComponent,
    ThreeLineAddressComponent,
    AccountAccessPipe,
    EnvironmentalActivityPipe,
    EventTypePipe,
    AccessRightsPipe,
    IssuanceTransactionSummaryTableComponent,
    ItlNotificationSummaryComponent,
    UkRadioInputComponent,
    QuantityTableComponent,
    AccountSummaryComponent,
    SkipLinkComponent,
    TrustedAccountPipe,
    SideMenuComponent,
    ErrorSummaryContainerComponent,
    TrustedAccountTableComponent,
    CancelUpdateRequestComponent,
    RequestSubmittedComponent,
    UnitTypeSopRenderPipe,
    CancelRequestLinkComponent,
    AuthRepContactComponent,
    UkSelectAuthorisedRepresentativeComponent,
    ArUpdateUserComponent,
    ArUpdateAccessRightsComponent,
    ArUpdateTypeComponent,
    AuthorisedRepresentativeUpdateTypePipe,
    GdsDateTimeShortPipe,
    GdsTimePipe,
    GdsTimeUTCPipe,
    GdsDateTimePipe,
    GdsDateShortPipe,
    GdsDateShortNoYearPipe,
    GdsDatePipe,
    GdsDateTimeUTCPipe,
    DomainEventsComponent,
    ArraySumPipe,
    AllowanceQuantityTableComponent,
    ApiEnumTypesPipe,
    AllocationCategoryPipe,
    UkSelectFileComponent,
    SearchTransactionsResultsComponent,
    RulesForAuthorisedRepresentativeComponent,
    AboutTrustedAccountListComponent,
    IdentificationDocumentationListComponent,
    RequestDocPipe,
    SafeUrlPipe,
    UkSingleCheckboxComponent,
    TransactionSigningDetailsComponent,
    AccountHolderSummaryChangesComponent,
    UkSingleCheckboxComponent,
    AccountHolderIndividualDetailsComponent,
    AccountHolderOrganisationDetailsComponent,
    AccountHolderOrganisationAddressComponent,
    AccountHolderIndividualContactDetailsComponent,
    AccountHolderSummaryChangesComponent,
    AccountHolderContactDetailsComponent,
    AccountHolderContactWorkDetailsComponent,
    SubWizardTitleComponent,
    MyProfileComponent,
    SummaryListComponent,
    FeatureHeaderWrapperComponent,
    CookiesPopUpComponent,
    CookiesPopUpContainerComponent,
    GovukTimePipe,
    ConcatDateTimePipe,
    GovukTagComponent,
    NavMenuComponent,
    SubmenuContainerComponent,
    ReconciliationStartComponent,
    DisableControlDirective,
    PasswordRevealDirective,
    ScreenReaderPageAnnounceDirective,
    IndividualFullNamePipe,
    IndividualFirstAndMiddleNamesPipe,
    TransactionAtrributesPipe,
    SearchReportButtonComponent,
    ReportSuccessBannerComponent,
    InstallationInputComponent,
    AircraftInputComponent,
    OperatorSummaryChangesComponent,
    SpinnerComponent,
    PersonalDetailsComponent,
    WorkDetailsComponent,
    MemorablePhraseComponent,
    PersonalDetailsSummaryChangesComponent,
    UserDeactivationSummaryComponent,
    WorkDetailsSummaryChangesComponent,
    MemorablePhraseSummaryChangesComponent,
    CountryNamePipe,
    CountryNameAsyncPipe,
    AccountDetailsSummaryComponent,
    SubmitSuccessChangeDescriptionComponent,
    TransactionConnectionSummaryComponent,
    UkRichTextEditorComponent,
    NotificationHeaderComponent,
    GovukNotificationBannerComponent,
    ArDisplayNamePipe,
    KeycloakUserDisplayNamePipe,
    AuthorisedRepresentativeDetailsPipe,
    NotificationRendererComponent,
    SetTransactionReferenceComponent,
    TransactionReferenceComponent,
    TransactionReferenceWarningComponent,
    AccountClosureWarningsComponent,
    NatAndNerAllocationDetailsTableComponent,
    CheckAllocationDetailsComponent,
    EmptySearchResultsComponent,
    ReadMoreComponent,
    NotesListComponent,
  ],
  providers: [
    TypeAheadService,
    AccountAccessService,
    ApiErrorHandlingService,
    TransactionApiService,
    TransactionWizardApiService,
    AuthorisedRepresentativeUpdateTypePipe,
    ExportFileService,
    FormatUkDatePipe,
    CountryNamePipe,
    CountryNameAsyncPipe,
    DatePipe,
    DateOfBirthPipe,
    NotificationApiService,
    NotesApiService,
  ],
})
export class SharedModule {}
