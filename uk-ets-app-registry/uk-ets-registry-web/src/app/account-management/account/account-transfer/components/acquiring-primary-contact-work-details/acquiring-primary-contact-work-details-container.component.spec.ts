import {
  ComponentFixture,
  ComponentFixtureAutoDetect,
  TestBed,
} from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import {
  FormatUkDatePipe,
  IndividualFirstAndMiddleNamesPipe,
  IndividualPipe,
  OrganisationPipe,
} from '@shared/pipes';
import { routes } from '@account-transfer/account-transfer-routing.module';

import { CheckAccountTransferComponent } from '@account-transfer/components/check-account-transfer/check-account-transfer.component';
import {
  SelectAccountTransferAcquiringAccountHolderComponent,
  SelectAccountTransferAcquiringAccountHolderContainerComponent,
} from '@account-transfer/components/select-account-transfer-acquiring-account-holder';
import { CancelAccountTransferContainerComponent } from '@account-transfer/components/cancel-account-transfer';
import { CheckAccountTransferContainerComponent } from '@account-transfer/components/check-account-transfer/check-account-transfer-container.component';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { TypeAheadComponent } from '@shared/form-controls/type-ahead/type-ahead.component';
import { CancelUpdateRequestComponent } from '@shared/components/cancel-update-request';
import { SubmittedAccountTransferContainerComponent } from '@account-transfer/components/submitted-account-transfer';
import { RequestSubmittedComponent } from '@shared/components/account/request-submitted';
import { AcquiringAccountHolderContactSummaryComponent } from '@account-transfer-shared/components/acquiring-account-holder-contact-summary';
import { AcquiringAccountHolderIndividualSummaryComponent } from '@account-transfer-shared/components/acquiring-account-holder-individual-summary';
import { AcquiringAccountHolderOrganisationSummaryComponent } from '@account-transfer-shared/components/acquiring-account-holder-organisation-summary';
import { TransferringAccountHolderSummaryComponent } from '@account-transfer-shared/components/transferring-account-holder-summary';
import { AcquiringOrganisationDetailsContainerComponent } from '@account-transfer/components/acquiring-organisation-details';
import { AcquiringOrganisationDetailsAddressContainerComponent } from '@account-transfer/components/acquiring-organisation-details-address';
import { AcquiringPrimaryContactDetailsContainerComponent } from '@account-transfer/components/acquiring-primary-contact-details';
import { AcquiringPrimaryContactWorkDetailsContainerComponent } from '@account-transfer/components/acquiring-primary-contact-work-details';
import { AccountHolderOrganisationDetailsComponent } from '@shared/components/account/account-holder-organisation-details';
import { AccountHolderOrganisationAddressComponent } from '@shared/components/account/account-holder-organisation-address';
import { AccountHolderContactDetailsComponent } from '@shared/components/account/account-holder-contact-details';
import { AccountHolderContactWorkDetailsComponent } from '@shared/components/account/account-holder-contact-work-details';
import {
  UkProtoFormEmailComponent,
  UkProtoFormSelectComponent,
  UkProtoFormTextareaComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { UKSelectPhoneComponent } from '@shared/form-controls';
import { SubWizardTitleComponent } from '@shared/components/account/sub-wizard-title';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { UkSingleCheckboxComponent } from '@shared/form-controls/uk-single-checkbox/uk-single-checkbox.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { provideMockStore } from '@ngrx/store/testing';
import { APP_BASE_HREF } from '@angular/common';
import { FormatTypeAheadAccountHolderResultPipe } from '@account-shared/pipes';
import { AccountTransferRequestSubmittedComponent } from '@account-transfer/components/account-transfer-request-submitted';

describe('AcquiringPrimaryContactWorkDetailsContainerComponent', () => {
  let component: AcquiringPrimaryContactWorkDetailsContainerComponent;
  let fixture: ComponentFixture<AcquiringPrimaryContactWorkDetailsContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        NgbTypeaheadModule,
        RouterTestingModule.withRoutes(routes),
      ],
      declarations: [
        CheckAccountTransferComponent,
        CheckAccountTransferContainerComponent,
        SelectAccountTransferAcquiringAccountHolderComponent,
        SelectAccountTransferAcquiringAccountHolderContainerComponent,
        CancelAccountTransferContainerComponent,
        CancelRequestLinkComponent,
        CancelUpdateRequestComponent,
        RequestSubmittedComponent,
        SubmittedAccountTransferContainerComponent,
        TypeAheadComponent,
        OrganisationPipe,
        IndividualPipe,
        IndividualFirstAndMiddleNamesPipe,
        ScreenReaderPageAnnounceDirective,
        AcquiringAccountHolderIndividualSummaryComponent,
        AcquiringAccountHolderOrganisationSummaryComponent,
        TransferringAccountHolderSummaryComponent,
        AcquiringAccountHolderContactSummaryComponent,
        AcquiringOrganisationDetailsContainerComponent,
        AccountHolderOrganisationDetailsComponent,
        AcquiringOrganisationDetailsAddressContainerComponent,
        AccountHolderOrganisationAddressComponent,
        AcquiringPrimaryContactDetailsContainerComponent,
        AccountHolderContactDetailsComponent,
        AcquiringPrimaryContactWorkDetailsContainerComponent,
        AccountHolderContactWorkDetailsComponent,
        UKSelectPhoneComponent,
        UkProtoFormTextComponent,
        UkProtoFormTextareaComponent,
        UkProtoFormSelectComponent,
        UkProtoFormEmailComponent,
        UkSingleCheckboxComponent,
        SubWizardTitleComponent,
        ConnectFormDirective,
        DisableControlDirective,
        FormatUkDatePipe,
        FormatTypeAheadAccountHolderResultPipe,
        AccountTransferRequestSubmittedComponent,
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: ComponentFixtureAutoDetect, useValue: true },
        provideMockStore(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(
      AcquiringPrimaryContactWorkDetailsContainerComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
