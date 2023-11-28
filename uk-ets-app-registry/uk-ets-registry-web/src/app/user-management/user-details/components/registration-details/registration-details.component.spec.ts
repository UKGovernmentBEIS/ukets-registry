import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationDetailsComponent } from './registration-details.component';
import { RouterTestingModule } from '@angular/router/testing';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { userStatusMap } from '@shared/user';

describe('RegistrationDetailsComponent', () => {
  let component: RegistrationDetailsComponent;
  let fixture: ComponentFixture<RegistrationDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RegistrationDetailsComponent, GovukTagComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistrationDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.user = {
      username: 'test@test.com',
      email: 'test@test.com',
      firstName: 'test name',
      lastName: 'test last name',
      userRoles: [],
      eligibleForSpecificActions: false,
      attributes: {
        urid: [],
        alsoKnownAs: [],
        buildingAndStreet: null,
        buildingAndStreetOptional: null,
        buildingAndStreetOptional2: null,
        country: [],
        countryOfBirth: [],
        postCode: [],
        townOrCity: [],
        stateOrProvince: [],
        birthDate: null,
        state: ['ACTIVE'],
        workBuildingAndStreet: null,
        workBuildingAndStreetOptional: null,
        workBuildingAndStreetOptional2: null,
        workCountry: [],
        workCountryCode: [],
        workEmailAddress: [],
        workEmailAddressConfirmation: [],
        workPhoneNumber: [],
        workPostCode: [],
        workTownOrCity: [],
        workStateOrProvince: [],
        memorablePhrase: '',
      },
    };
    fixture.componentInstance.enrolmentKeyDetails = null;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    const component: RegistrationDetailsComponent =
      new RegistrationDetailsComponent();
    expect(component).toBeDefined();
  });

  it('should be equal to account identifier 1002', () => {
    expect(fixture.componentInstance.user.username).toEqual('test@test.com');
  });
});
