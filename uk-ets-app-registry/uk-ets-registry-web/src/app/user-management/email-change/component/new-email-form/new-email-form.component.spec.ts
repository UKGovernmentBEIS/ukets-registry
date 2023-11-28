import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewEmailFormComponent } from './new-email-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { EmailChangeState } from '@email-change/reducer';
import { canGoBack } from '@shared/shared.action';
import {
  UkProtoFormEmailComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';

describe('EnterNewEmailFormComponent', () => {
  let component: NewEmailFormComponent;
  let fixture: ComponentFixture<NewEmailFormComponent>;
  let mockStore: MockStore<EmailChangeState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule],
      declarations: [
        UkProtoFormTextComponent,
        UkProtoFormEmailComponent,
        NewEmailFormComponent,
        DisableControlDirective,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(NewEmailFormComponent);
  });

  it('should create and dispatch canGoBack with go back route the route of caller', () => {
    const spyStore = spyOn(mockStore, 'dispatch');
    component = fixture.componentInstance;
    component.state = {
      newEmail: null,
      urid: 'UK14234',
      caller: {
        route: '',
      },
      confirmation: null,
      confirmationLoaded: false,
    };
    component.currentUser = {
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
        workCountryCode: [],
        workEmailAddress: [],
        workEmailAddressConfirmation: [],
        workPhoneNumber: [],
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
    };
    const action = canGoBack({
      goBackRoute: component.state.caller.route,
      extras: component.state.caller.extras,
    });
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(spyStore).toHaveBeenCalledWith(action);
  });
});
