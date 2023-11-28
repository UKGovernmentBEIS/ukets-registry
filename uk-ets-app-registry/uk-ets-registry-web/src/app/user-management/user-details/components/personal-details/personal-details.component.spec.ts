import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalDetailsComponent } from './personal-details.component';
import {
  CountryNameAsyncPipe,
  DateOfBirthPipe,
  FormatUkDatePipe,
} from '@shared/pipes';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { UkDate } from '@shared/model/uk-date';
import { SharedModule } from '@registry-web/shared/shared.module';
import { provideMockStore } from '@ngrx/store/testing';

class DateFormatPipeMock {
  transform(): UkDate {
    return { day: '08', month: '09', year: '1985' };
  }
}

const REMOVED_DUE_TO_STATUS = 'REMOVED_DUE_TO_STATUS';

describe('PersonalDetailsComponent', () => {
  let component: PersonalDetailsComponent;
  let fixture: ComponentFixture<PersonalDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [
        PersonalDetailsComponent,
        DateOfBirthPipe,
        ThreeLineAddressComponent,
        FormatUkDatePipe,
      ],
      providers: [
        { provide: DateOfBirthPipe, useValue: DateFormatPipeMock },
        CountryNameAsyncPipe,
        provideMockStore(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.user = {
      username: 'test@test.com',
      email: '',
      firstName: '',
      lastName: '',
      userRoles: [],
      eligibleForSpecificActions: false,
      userRoles: [],
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
        state: [],
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
      },
    } as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    const component: PersonalDetailsComponent = new PersonalDetailsComponent();
    expect(component).toBeDefined();
  });

  it('should be equal to username test@test.com', () => {
    expect(fixture.componentInstance.user.username).toEqual('test@test.com');
  });

  it('should be equal to month 5, day 1 and year 1980', () => {
    const datePipe = new DateOfBirthPipe().transform('01/05/1980');
    expect(datePipe.day).toEqual('1');
    expect(datePipe.month).toEqual('5');
    expect(datePipe.year).toEqual('1980');
  });

  it('should hide address when attributes are null or REMOVED_DUE_TO_STATUS', () => {
    const user: any = {
      attributes: {
        buildingAndStreet: [REMOVED_DUE_TO_STATUS],
        buildingAndStreetOptional: [REMOVED_DUE_TO_STATUS],
        buildingAndStreetOptional2: [REMOVED_DUE_TO_STATUS],
        townOrCity: [REMOVED_DUE_TO_STATUS],
        stateOrProvince: [REMOVED_DUE_TO_STATUS],
        country: [REMOVED_DUE_TO_STATUS],
        postCode: [REMOVED_DUE_TO_STATUS],
      },
    };
    component.user = user;

    component.ngOnInit();

    expect(component.hideHomeAddress).toBe(true);
  });
});
