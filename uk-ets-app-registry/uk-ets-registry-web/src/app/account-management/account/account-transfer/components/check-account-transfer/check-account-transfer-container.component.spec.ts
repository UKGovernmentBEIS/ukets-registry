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
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { APP_BASE_HREF } from '@angular/common';
import {
  AccountTransferState,
  selectAcquiringAccountHolder,
  selectAcquiringAccountHolderPrimaryContact,
  selectTransferringAccountHolder,
} from '@account-transfer/store/reducers';
import { AccountHolderType } from '@shared/model/account';
import { FormatTypeAheadAccountHolderResultPipe } from '@account-shared/pipes';
import { AccountTransferRequestSubmittedComponent } from '@account-transfer/components/account-transfer-request-submitted';

describe('CheckAccountTransferContainerComponent', () => {
  let component: CheckAccountTransferContainerComponent;
  let fixture: ComponentFixture<CheckAccountTransferContainerComponent>;
  let mockStore: MockStore;
  const initialState: AccountTransferState = {
    updateType: 'ACCOUNT_TRANSFER_TO_CREATED_HOLDER',
    acquiringAccountHolder: {
      id: 9,
      type: AccountHolderType.ORGANISATION,
      details: {
        name: 'NRB Group',
        registrationNumber: '',
        noRegistrationNumJustification: 'Belgium located',
      },
      address: null,
    },
    acquiringAccountHolderContactInfo: {
      primaryContact: {
        id: 5,
        new: false,
        details: {
          firstName: 'Primary Contact first name',
          lastName: 'Primary contact last name',
          aka: '',
          birthDate: null,
          isOverEighteen: true,
        },
        positionInCompany: 'CEO',
        address: {
          buildingAndStreet: 'street 1',
          buildingAndStreet2: '',
          buildingAndStreet3: '',
          townOrCity: '',
          stateOrProvince: '',
          country: 'GB',
          postCode: '998765',
        },
        phoneNumber: {
          countryCode1: '',
          phoneNumber1: '',
          countryCode2: '',
          phoneNumber2: '',
        },
        emailAddress: {
          emailAddress: '',
          emailAddressConfirmation: '',
        },
      },
      alternativeContact: null,
    },
    submittedRequestIdentifier: null,
  };

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
        provideMockStore({ initialState }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    mockStore = TestBed.inject(MockStore);
    mockStore.overrideSelector(
      selectAcquiringAccountHolder,
      initialState.acquiringAccountHolder
    );
    mockStore.overrideSelector(
      selectAcquiringAccountHolderPrimaryContact,
      initialState.acquiringAccountHolderContactInfo.primaryContact
    );
    mockStore.overrideSelector(selectTransferringAccountHolder, {
      id: 41,
      type: AccountHolderType.ORGANISATION,
      details: {
        name: 'Omikron One',
        registrationNumber: '',
        noRegistrationNumJustification: 'Not yet established',
      },
      address: null,
      status: null,
    });
    mockStore.refreshState();
    fixture = TestBed.createComponent(CheckAccountTransferContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
