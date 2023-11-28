import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckDeactivationDetailsComponent } from './check-deactivation-details.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormGroupDirective } from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { UserDetailsUpdatePipe } from '@user-update/pipes';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { CountryNamePipe, FormatUkDatePipe } from '@shared/pipes';
import { UserUpdateDetailsType } from '@user-update/model';
import { KeycloakUser } from '@shared/user';
import { ARAccessRights } from '@shared/model/account';

describe('CheckDeactivationDetailsComponent', () => {
  let component: CheckDeactivationDetailsComponent;
  let fixture: ComponentFixture<CheckDeactivationDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        CheckDeactivationDetailsComponent,
        FormGroupDirective,
        ConnectFormDirective,
        UserDetailsUpdatePipe,
        ScreenReaderPageAnnounceDirective,
      ],
      providers: [FormatUkDatePipe, CountryNamePipe, UserDetailsUpdatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckDeactivationDetailsComponent);
    component = fixture.componentInstance;
    component.countries = [];
    component.userUpdateDetailsType = UserUpdateDetailsType.UPDATE_USER_DETAILS;
    component.userDetails = {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      eligibleForSpecificActions: false,
      attributes: {
        urid: [''],
        alsoKnownAs: [''],
        buildingAndStreet: [''],
        buildingAndStreetOptional: [''],
        buildingAndStreetOptional2: [''],
        country: [''],
        countryOfBirth: [''],
        postCode: [''],
        townOrCity: [''],
        stateOrProvince: [''],
        birthDate: [''],
        state: [''],
        workBuildingAndStreet: [''],
        workBuildingAndStreetOptional: [''],
        workBuildingAndStreetOptional2: [''],
        workCountry: [''],
        workCountryCode: [''],
        workEmailAddress: [''],
        workEmailAddressConfirmation: [''],
        workPhoneNumber: [''],
        workPostCode: [''],
        workTownOrCity: [''],
        workStateOrProvince: [''],
        lastLoginDate: [''],
        memorablePhrase: '',
      },
    } as KeycloakUser;
    component.arInAccounts = [
      {
        accountName: 'accName',
        accountIdentifier: 12345,
        accountHolderName: 'accountHolderName',
        right: ARAccessRights.ROLE_BASED,
        state: 'ACTIVE',
      },
      {
        accountName: 'accName2',
        accountIdentifier: 123456,
        accountHolderName: 'accountHolderName2',
        right: ARAccessRights.ROLE_BASED,
        state: 'ACTIVE',
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
