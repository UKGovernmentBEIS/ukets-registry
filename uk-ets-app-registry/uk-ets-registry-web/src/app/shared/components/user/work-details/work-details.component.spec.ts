import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { WorkDetailsComponent } from './work-details.component';
import { User } from '@registry-web/shared/user';
import { SharedModule } from '@registry-web/shared/shared.module';

const formBuilder = new FormBuilder();

describe('WorkDetailsComponent', () => {
  let component: WorkDetailsComponent;
  let fixture: ComponentFixture<WorkDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [ReactiveFormsModule, SharedModule],
        declarations: [
          WorkDetailsComponent,
          FormGroupDirective,
          ScreenReaderPageAnnounceDirective,
          ConnectFormDirective,
        ],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkDetailsComponent);
    component = fixture.componentInstance;
    component.countries = [];
    component._user = new User();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update workCountryCode and workPhoneNumber when user input is set', () => {
    const user = new User();
    user.workCountryCode = 'UK';
    user.workPhoneNumber = '1234567890';
    component.user = user;
    expect(component._phoneInfo).toEqual({
      countryCode: 'UK',
      phoneNumber: '1234567890',
    });
  });

  it('should update formGroup values when workCountry changes', () => {
    const workPostCodeControl = component.formGroup.get('workPostCode');
    component.formGroup.patchValue({ workCountry: 'US' });
    expect(workPostCodeControl.validator).toBeNull();
    component.formGroup.patchValue({ workCountry: 'UK' });
    expect(workPostCodeControl.validator).toBe(Validators.required);
  });

  it('should update workEmailAddress and workEmailAddressConfirmation when sameEmail is checked', () => {
    const user = new User();
    user.emailAddress = 'test@email.com';
    component._user = user;
    component.sameEmail = true;
    component.ngAfterViewInit();

    expect(component.formGroup.get('workEmailAddress').value).toBe(
      'test@email.com'
    );
    expect(component.formGroup.get('workEmailAddressConfirmation').value).toBe(
      'test@email.com'
    );
  });

  it('should update work address when sameAddress is checked', () => {
    const user = new User();
    user.buildingAndStreet = '123 Street';
    user.buildingAndStreetOptional = 'Apt 4';
    user.townOrCity = 'City';
    user.postCode = '12345';
    user.country = 'UK';
    component._user = user;
    component.sameAddress = true;
    component.ngAfterViewInit();

    expect(component.formGroup.get('workBuildingAndStreet').value).toBe(
      '123 Street'
    );
    expect(component.formGroup.get('workBuildingAndStreetOptional').value).toBe(
      'Apt 4'
    );
    expect(component.formGroup.get('workTownOrCity').value).toBe('City');
    expect(component.formGroup.get('workPostCode').value).toBe('12345');
    expect(component.formGroup.get('workCountry').value).toBe('UK');
  });
});
