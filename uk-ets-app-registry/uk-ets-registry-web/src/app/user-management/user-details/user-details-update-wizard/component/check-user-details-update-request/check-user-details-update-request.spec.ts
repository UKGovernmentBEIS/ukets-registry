import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormGroupDirective } from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { CountryNamePipe, FormatUkDatePipe } from '@shared/pipes';
import { IUser } from '@shared/user';
import { CheckUserDetailsUpdateRequestComponent } from './check-user-details-update-request-component';
import { UserDetailsUpdatePipe } from '@user-update/pipes';
import { UserUpdateDetailsType } from '@user-update/model';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

describe('CheckUpdateRequestComponent', () => {
  let component: CheckUserDetailsUpdateRequestComponent;
  let fixture: ComponentFixture<CheckUserDetailsUpdateRequestComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        CheckUserDetailsUpdateRequestComponent,
        FormGroupDirective,
        ConnectFormDirective,
        UserDetailsUpdatePipe,
        ScreenReaderPageAnnounceDirective,
      ],
      providers: [FormatUkDatePipe, CountryNamePipe, UserDetailsUpdatePipe],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUserDetailsUpdateRequestComponent);
    component = fixture.componentInstance;
    component.countries = [];
    component.userUpdateDetailsType = UserUpdateDetailsType.UPDATE_USER_DETAILS;
    component.currentUserDetails = {
      emailAddress: '',
      emailAddressConfirmation: '',
      userId: '',
      username: '',
      password: '',
      firstName: '',
      lastName: '',
      alsoKnownAs: '',
      countryOfBirth: '',
      workMobileCountryCode: '',
      workMobilePhoneNumber: '',
      workAlternativeCountryCode: '',
      workAlternativePhoneNumber: '',
      noMobilePhoneNumberReason: '',
      workBuildingAndStreet: '',
      workBuildingAndStreetOptional: '',
      workBuildingAndStreetOptional2: '',
      workTownOrCity: '',
      workStateOrProvince: '',
      workPostCode: '',
      workCountry: '',
      urid: '',
      state: '',
      status: 'ENROLLED',
      memorablePhrase: '',
    } as IUser;
    component.newUserDetails = {} as IUser;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
