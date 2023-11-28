import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormBuilder, FormGroupDirective } from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { PersonalDetailsSummaryChangesComponent } from './personal-details-summary-changes-component';
import { CountryNamePipe, FormatUkDatePipe } from '@shared/pipes';
import { IUser } from '@shared/user';
import { RouterTestingModule } from '@angular/router/testing';

const formBuilder = new FormBuilder();

describe('PersonalDetailsSummaryChangesComponent', () => {
  let component: PersonalDetailsSummaryChangesComponent;
  let fixture: ComponentFixture<PersonalDetailsSummaryChangesComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [RouterTestingModule],
        declarations: [
          PersonalDetailsSummaryChangesComponent,
          FormGroupDirective,
          ConnectFormDirective,
        ],
        providers: [
          { provide: FormBuilder, useValue: formBuilder },
          FormatUkDatePipe,
          CountryNamePipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalDetailsSummaryChangesComponent);
    component = fixture.componentInstance;
    component.isWizardOrientedFlag = false;
    component.countries = [];
    component.current = {
      emailAddress: '',
      emailAddressConfirmation: '',
      userId: '',
      username: '',
      password: '',
      firstName: '',
      lastName: '',
      alsoKnownAs: '',
      countryOfBirth: '',
      workCountryCode: '',
      workPhoneNumber: '',
      workEmailAddress: '',
      workEmailAddressConfirmation: '',
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
    component.changed = {} as IUser;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
