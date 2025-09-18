import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormBuilder, FormGroupDirective } from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { CountryNamePipe, FormatUkDatePipe } from '@shared/pipes';
import { IUser } from '@shared/user';
import { RouterTestingModule } from '@angular/router/testing';
import { MemorablePhraseSummaryChangesComponent } from '@shared/components/user/memorable-phrase-summary-changes/memorable-phrase-summary-changes.component';

const formBuilder = new FormBuilder();

describe('MemorablePhraseSummaryChangesComponent', () => {
  let component: MemorablePhraseSummaryChangesComponent;
  let fixture: ComponentFixture<MemorablePhraseSummaryChangesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [RouterTestingModule],
      declarations: [
        MemorablePhraseSummaryChangesComponent,
        FormGroupDirective,
        ConnectFormDirective,
      ],
      providers: [
        { provide: FormBuilder, useValue: formBuilder },
        FormatUkDatePipe,
        CountryNamePipe,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemorablePhraseSummaryChangesComponent);
    component = fixture.componentInstance;
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
    component.changed = {} as IUser;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
