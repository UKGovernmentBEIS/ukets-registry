import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkContactDetailsComponent } from './work-contact-details.component';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { CountryNameAsyncPipe } from '@registry-web/shared/pipes';
import { SharedModule } from '@registry-web/shared/shared.module';
import { provideMockStore } from '@ngrx/store/testing';

const REMOVED_DUE_TO_STATUS = 'REMOVED_DUE_TO_STATUS';

describe('WorkContactDetailsComponent', () => {
  let component: WorkContactDetailsComponent;
  let fixture: ComponentFixture<WorkContactDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [
        WorkContactDetailsComponent,
        ThreeLineAddressComponent,
        PhoneNumberComponent,
      ],
      providers: [CountryNameAsyncPipe, provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkContactDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.user = {
      username: 'test@test.com',
      email: '',
      firstName: '',
      lastName: '',
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
        state: [],
        workBuildingAndStreet: ['work building street 1'],
        workBuildingAndStreetOptional: null,
        workBuildingAndStreetOptional2: null,
        workCountry: ['Greece'],
        workCountryCode: ['GR'],
        workEmailAddress: ['test@test.com'],
        workEmailAddressConfirmation: ['test@test.com'],
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
    const component: WorkContactDetailsComponent =
      new WorkContactDetailsComponent();
    expect(component).toBeDefined();
  });

  it('should hide address when attributes are null or REMOVED_DUE_TO_STATUS', () => {
    const user: any = {
      attributes: {
        workBuildingAndStreet: [REMOVED_DUE_TO_STATUS],
        workBuildingAndStreetOptional: [REMOVED_DUE_TO_STATUS],
        workBuildingAndStreetOptional2: [REMOVED_DUE_TO_STATUS],
        workTownOrCity: [REMOVED_DUE_TO_STATUS],
        workStateOrProvince: [REMOVED_DUE_TO_STATUS],
        workCountry: [REMOVED_DUE_TO_STATUS],
        workPostCode: [REMOVED_DUE_TO_STATUS],
      },
    };
    component.user = user;

    component.ngOnInit();

    expect(component.hideWorkAddress).toBe(true);
  });
});
