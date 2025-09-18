import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {
  NewEmailFormComponent,
  NewEmailFormContainerComponent,
} from '@email-change/component';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { EmailChangeState } from '@email-change/reducer';
import { selectState } from '@email-change/reducer/email-change.selector';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { UkProtoFormEmailComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-email/uk-proto-form-email.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';

describe('EnterNewEmailFormContainerComponent', () => {
  let component: NewEmailFormContainerComponent;
  let fixture: ComponentFixture<NewEmailFormContainerComponent>;
  let mockStore: MockStore<EmailChangeState>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [
        UkProtoFormTextComponent,
        UkProtoFormEmailComponent,
        NewEmailFormComponent,
        NewEmailFormContainerComponent,
        DisableControlDirective,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewEmailFormContainerComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    mockStore.overrideSelector(selectState, {
      urid: 'UK1234',
      caller: {
        route: '',
      },
      newEmail: null,
      confirmation: null,
      confirmationLoaded: false,
    });
    mockStore.overrideSelector(selectUserDetails, {
      attributes: {
        alsoKnownAs: [],
        birthDate: [],
        buildingAndStreet: [],
        buildingAndStreetOptional: [],
        buildingAndStreetOptional2: [],
        country: [],
        countryOfBirth: [],
        postCode: [],
        state: [],
        townOrCity: [],
        stateOrProvince: [],
        urid: [],
        workBuildingAndStreet: [],
        workBuildingAndStreetOptional: [],
        workBuildingAndStreetOptional2: [],
        workCountry: [],
        workMobileCountryCode: [],
        workMobilePhoneNumber: [],
        workAlternativeCountryCode: [],
        workAlternativePhoneNumber: [],
        noMobilePhoneNumberReason: [],
        workPostCode: [],
        workTownOrCity: [],
        workStateOrProvince: [],
      },
      userRoles: [],
      eligibleForSpecificActions: false,
      email: 'test@test.com',
      firstName: '',
      lastName: '',
      username: '',
    });
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
