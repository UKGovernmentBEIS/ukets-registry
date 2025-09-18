import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserDetailsComponent } from './user-details.component';
import { SideMenuComponent } from '@shared/side-menu/side-menu.component';
import {
  ArInAccountsComponent,
  ArInAccountsContainerComponent,
  IdentificationDocumentationComponent,
  PersonalDetailsComponent,
  RegistrationDetailsComponent,
  WorkContactDetailsComponent,
} from '@user-management/user-details/components';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import {
  UserDetailsSideMenu,
  ViewMode,
} from '@user-management/user-details/model';
import {
  AccessRightsPipe,
  CountryNameAsyncPipe,
  DateOfBirthPipe,
  EventTypePipe,
  FormatUkDatePipe,
  GdsDateTimePipe,
  GdsDateTimeShortPipe,
  ProtectPipe,
} from '@shared/pipes';
import { UkDate } from '@shared/model/uk-date';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { By } from '@angular/platform-browser';
import { DomainEventsComponent } from '@shared/components/event/domain-events/domain-events.component';
import { Router } from '@angular/router';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { AuthApiService } from '../../../../auth/auth-api.service';
import { IdentificationDocumentationListComponent } from '@shared/components/identification-documentation-list';
import { RequestDocPipe } from '@shared/pipes/request-doc.pipe';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { BannerComponent } from '@registry-web/shared/banner/banner.component';
import { KeycloakUserDisplayNamePipe } from '@shared/pipes';
import { SharedModule } from '@registry-web/shared/shared.module';
import { selectAllCountries } from '@registry-web/shared/shared.selector';
import { RecoveryMethodsComponent } from '../recovery-methods/recovery-methods.component';

class DateFormatPipeMock {
  transform(): UkDate {
    return { day: '08', month: '09', year: '1985' };
  }
}

describe('UserDetailsComponent', () => {
  let component: UserDetailsComponent;
  let fixture: ComponentFixture<UserDetailsComponent>;
  let store: MockStore<any>;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        GdsDateTimePipe,
        UserDetailsComponent,
        SideMenuComponent,
        RegistrationDetailsComponent,
        PersonalDetailsComponent,
        RecoveryMethodsComponent,
        WorkContactDetailsComponent,
        IdentificationDocumentationListComponent,
        IdentificationDocumentationComponent,
        ArInAccountsContainerComponent,
        ArInAccountsComponent,
        DomainEventsComponent,
        BackToTopComponent,
        ThreeLineAddressComponent,
        DateOfBirthPipe,
        EventTypePipe,
        AccessRightsPipe,
        PhoneNumberComponent,
        FormatUkDatePipe,
        GdsDateTimeShortPipe,
        ProtectPipe,
        RequestDocPipe,
        KeycloakUserDisplayNamePipe,
        GovukTagComponent,
        BannerComponent,
      ],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: DateOfBirthPipe, useValue: DateFormatPipeMock },
        { provide: AuthApiService, useValue: MockAuthApiService },
        KeycloakUserDisplayNamePipe,
        CountryNameAsyncPipe,
        provideMockStore({
          selectors: [
            {
              selector: selectAllCountries,
              value: [
                {
                  'index-entry-number': '',
                  'entry-number': '',
                  'entry-timestamp': '',
                  key: 'GR',
                  item: [
                    {
                      country: 'GR',
                      'end-date': '',
                      'official-name': '',
                      name: 'Greece',
                      'citizen-names': '',
                    },
                  ],
                  callingCode: '',
                },
              ],
            },
          ],
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.sideMenuItems =
      UserDetailsSideMenu.UserDetailItems;
    fixture.componentInstance.user = {
      username: 'user_details_test',
      email: 'ukets_user@gov.uk',
      firstName: 'James',
      userRoles: [],
      lastName: 'Bond',
      eligibleForSpecificActions: false,
      attributes: {
        urid: ['UK230169410292'],
        alsoKnownAs: ['also known as Value'],
        buildingAndStreet: ['Ethnikis Antistasis 67'],
        buildingAndStreetOptional: [''],
        buildingAndStreetOptional2: [''],
        country: ['GR'],
        countryOfBirth: ['GR'],
        postCode: ['15231'],
        townOrCity: ['Athens'],
        stateOrProvince: ['Attica'],
        birthDate: ['3/31/1990'],
        state: ['ACTIVE'],
        workBuildingAndStreet: ['Kifisias 8'],
        workBuildingAndStreetOptional: [''],
        workBuildingAndStreetOptional2: [''],
        workCountry: ['GR'],
        workMobileCountryCode: ['GR (30)'],
        workMobilePhoneNumber: ['22224222'],
        workAlternativeCountryCode: [''],
        workAlternativePhoneNumber: [''],
        noMobilePhoneNumberReason: [''],
        workPostCode: [''],
        workTownOrCity: ['Athens'],
        workStateOrProvince: ['Attica'],
        lastLoginDate: [new Date().toISOString()],
        recoveryCountryCode: ['GR (30)'],
        recoveryPhoneNumber: ['6912345678'],
        recoveryEmailAddress: ['ukets_user_recovery@gov.uk'],
        hideRecoveryMethodsNotification: ['true'],
        // differentCountryLastFiveYears: [false],
      },
    };
    fixture.componentInstance.scrollToRecoverySection = false;
    fixture.componentInstance.initiatorUrid = 'UK230169410292';
    fixture.componentInstance.currentViewMode = ViewMode.USER_DETAILS;
    store = TestBed.inject(Store) as MockStore<any>;
    router = TestBed.inject(Router);
    Object.defineProperty(window, 'history', {
      value: {
        state: { scrollToRecoverySection: false },
      },
      writable: true,
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate UserDetailsComponent', () => {
    const pipeComponent: KeycloakUserDisplayNamePipe =
      new KeycloakUserDisplayNamePipe();
    const testComponent: UserDetailsComponent = new UserDetailsComponent(
      pipeComponent
    );
    expect(testComponent).toBeDefined();
  });

  it('should instantiate RegistrationDetailsComponent', () => {
    const testComponent: RegistrationDetailsComponent =
      new RegistrationDetailsComponent();
    expect(testComponent).toBeDefined();
  });

  it('should instantiate PersonalDetailsComponent', () => {
    const testComponent: PersonalDetailsComponent =
      new PersonalDetailsComponent();
    expect(testComponent).toBeDefined();
  });

  it('should instantiate WorkContactDetailsComponent', () => {
    const testComponent: WorkContactDetailsComponent =
      new WorkContactDetailsComponent();
    expect(testComponent).toBeDefined();
  });

  it('should instantiate IdentificationDocumentationComponent', () => {
    const testComponent: IdentificationDocumentationComponent =
      new IdentificationDocumentationComponent();
    expect(testComponent).toBeDefined();
  });

  it('should instantiate ArInAccountsContainerComponent', () => {
    const testComponent: ArInAccountsContainerComponent =
      new ArInAccountsContainerComponent(store);
    expect(testComponent).toBeDefined();
  });

  it('should instantiate ArInAccountsComponent', () => {
    const testComponent: ArInAccountsComponent = new ArInAccountsComponent();
    expect(testComponent).toBeDefined();
  });

  it('should instantiate HistoryAndCommentsContainerComponent', () => {
    const testComponent: DomainEventsComponent = new DomainEventsComponent();
    expect(testComponent).toBeDefined();
  });

  it('should render User details title with govuk-heading-xl', () => {
    fixture.detectChanges();
    fixture.componentInstance.scrollToRecoverySection = false;
    const key = fixture.debugElement.queryAll(By.css('.govuk-heading-xl'))[0];
    expect(key.nativeElement.textContent).toContain('User details');
  });

  it('should render Registration details', () => {
    fixture.detectChanges();
    fixture.componentInstance.scrollToRecoverySection = false;
    const rows = fixture.debugElement.queryAll(
      By.css('app-registration-details .govuk-summary-list__row')
    );
    // rows.forEach((r, index) =>
    //   console.log(r.nativeElement.textContent + '    index: ' + index)
    // );
    const emailAddress = rows[2];
    const emailAddressKey = emailAddress.children[0];
    expect(emailAddressKey.nativeElement.textContent.trim()).toEqual(
      'Email address'
    );
    const emailAddressValue = emailAddress.children[1];
    expect(emailAddressValue.nativeElement.textContent.trim()).toEqual(
      'ukets_user@gov.uk'
    );

    const userID = rows[0];
    const userIDKey = userID.children[0];
    expect(userIDKey.nativeElement.textContent.trim()).toEqual('User ID');
    const userIDValue = userID.children[1];
    expect(userIDValue.nativeElement.textContent.trim()).toEqual(
      'UK230169410292'
    );
  });

  it('should render Personal details', () => {
    fixture.detectChanges();
    fixture.componentInstance.scrollToRecoverySection = false;
    const rows = fixture.debugElement.queryAll(
      By.css('app-personal-details .govuk-summary-list__row')
    );
    const firstName = rows[0];
    const firstNameKey = firstName.children[0];
    expect(firstNameKey.nativeElement.textContent.trim()).toEqual(
      'First and middle names'
    );
    const firstNameValue = firstName.children[1];
    expect(firstNameValue.nativeElement.textContent.trim()).toEqual('James');

    const lastName = rows[1];
    const lastNameKey = lastName.children[0];
    expect(lastNameKey.nativeElement.textContent.trim()).toEqual('Last name');
    const lastNameValue = lastName.children[1];
    expect(lastNameValue.nativeElement.textContent.trim()).toEqual('Bond');

    const alsoKnownAs = rows[2];
    const alsoKnownAsKey = alsoKnownAs.children[0];
    expect(alsoKnownAsKey.nativeElement.textContent.trim()).toEqual('Known as');
    const alsoKnownAsValue = alsoKnownAs.children[1];
    expect(alsoKnownAsValue.nativeElement.textContent.trim()).toEqual(
      'also known as Value'
    );

    const countryOfBirth = rows[10];
    const countryOfBirthKey = countryOfBirth.children[0];
    expect(countryOfBirthKey.nativeElement.textContent.trim()).toEqual(
      'Country of birth'
    );
    const countryOfBirthValue = countryOfBirth.children[1];
    expect(countryOfBirthValue.nativeElement.textContent.trim()).toEqual(
      'Greece'
    );
  });

  it('should render Recovery methods with edit controls', () => {
    const rows = fixture.debugElement.queryAll(
      By.css('app-recovery-methods .govuk-summary-list__row')
    );
    fixture.componentInstance.scrollToRecoverySection = false;

    const phoneRow = rows[0];
    const phoneKey = phoneRow.children[0];
    expect(phoneKey.nativeElement.textContent.trim()).toEqual(
      'Recovery phone number'
    );
    const phoneValue = phoneRow.children[1];
    expect(phoneValue.nativeElement.textContent.trim()).toEqual(
      'GR (30) 6912345678 Update your recovery phone number  Remove your recovery phone number'
    );

    const emailRow = rows[1];
    const emailKey = emailRow.children[0];
    expect(emailKey.nativeElement.textContent.trim()).toEqual(
      'Recovery email address'
    );
    const emailValue = emailRow.children[1];
    expect(emailValue.nativeElement.textContent.trim()).toEqual(
      'ukets_user_recovery@gov.uk  Update your recovery email address  Remove your recovery email address'
    );
  });

  it('should render Recovery methods without edit controls', () => {
    fixture.componentInstance.initiatorUrid = '';
    fixture.componentInstance.scrollToRecoverySection = false;
    fixture.detectChanges();
    const rows = fixture.debugElement.queryAll(
      By.css('app-recovery-methods .govuk-summary-list__row')
    );

    const phoneRow = rows[0];
    const phoneKey = phoneRow.children[0];
    expect(phoneKey.nativeElement.textContent.trim()).toEqual(
      'Recovery phone number'
    );
    const phoneValue = phoneRow.children[1];
    expect(phoneValue.nativeElement.textContent.trim()).toEqual(
      'GR (30) 6912345678'
    );

    const emailRow = rows[1];
    const emailKey = emailRow.children[0];
    expect(emailKey.nativeElement.textContent.trim()).toEqual(
      'Recovery email address'
    );
    const emailValue = emailRow.children[1];
    expect(emailValue.nativeElement.textContent.trim()).toEqual(
      'ukets_user_recovery@gov.uk'
    );
  });
});
