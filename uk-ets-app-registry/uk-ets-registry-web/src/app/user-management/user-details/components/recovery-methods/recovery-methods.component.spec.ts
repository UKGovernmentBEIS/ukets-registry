import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RecoveryMethodsComponent } from './recovery-methods.component';
import { SharedModule } from '@registry-web/shared/shared.module';

describe('RecoveryMethodsComponent', () => {
  let component: RecoveryMethodsComponent;
  let fixture: ComponentFixture<RecoveryMethodsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecoveryMethodsComponent],
      imports: [SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecoveryMethodsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.user = {
      username: 'test@test.com',
      email: 'test@test.com',
      firstName: 'test name',
      lastName: 'test last name',
      userRoles: [],
      eligibleForSpecificActions: false,
      attributes: {
        urid: ['UK405681794859'],
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
        workMobileCountryCode: [],
        workMobilePhoneNumber: [],
        workAlternativeCountryCode: [],
        workAlternativePhoneNumber: [],
        noMobilePhoneNumberReason: [],
        workEmailAddress: [],
        workEmailAddressConfirmation: [],
        workPostCode: [],
        workTownOrCity: [],
        workStateOrProvince: [],
        memorablePhrase: '',
      },
    };
    fixture.componentInstance.initiatorUrid = 'UK405681794859';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    const component = new RecoveryMethodsComponent();
    expect(component).toBeDefined();
  });

  it('should return canEdit true when initiatorUrid and urid match', () => {
    expect(fixture.componentInstance.canEdit).toBe(true);
  });

  it('should return canEdit false when initiatorUrid and urid dont match', () => {
    fixture.componentInstance.initiatorUrid = '';
    expect(fixture.componentInstance.canEdit).toBe(false);
  });
});
