import { FormBuilder, FormGroupDirective } from '@angular/forms';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { CountryNamePipe } from '@shared/pipes';
import { IUser } from '@shared/user';
import { WorkDetailsSummaryChangesComponent } from './work-details-summary-changes-component';
import { RouterTestingModule } from '@angular/router/testing';

const formBuilder = new FormBuilder();

describe('WorkDetailsSummaryChangesComponent', () => {
  let component: WorkDetailsSummaryChangesComponent;
  let fixture: ComponentFixture<WorkDetailsSummaryChangesComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [RouterTestingModule],
        declarations: [
          WorkDetailsSummaryChangesComponent,
          FormGroupDirective,
          ConnectFormDirective,
        ],
        providers: [
          { provide: FormBuilder, useValue: formBuilder },
          CountryNamePipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkDetailsSummaryChangesComponent);
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
