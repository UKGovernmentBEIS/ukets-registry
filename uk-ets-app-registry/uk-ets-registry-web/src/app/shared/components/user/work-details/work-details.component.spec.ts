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
import { UkRegistryValidators } from '@registry-web/shared/validation';

const formBuilder = new FormBuilder();

describe('WorkDetailsComponent', () => {
  let component: WorkDetailsComponent;
  let fixture: ComponentFixture<WorkDetailsComponent>;

  beforeEach(waitForAsync(() => {
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
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkDetailsComponent);
    component = fixture.componentInstance;
    component.countries = [];
    component.user = new User();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should require hasWorkMobilePhone', () => {
    const hasWorkMobilePhoneFC = component.formGroup.get('hasWorkMobilePhone');
    expect(hasWorkMobilePhoneFC.validator).toBe(Validators.required);
  });
  it('should make workMobilePhone required when hasWorkMobilePhone is true', () => {
    component.hasWorkMobilePhone = true;
    component.ngOnInit();

    expect(component.formGroup.get('workMobilePhone').validator).toBe(
      UkRegistryValidators.allFieldsRequired
    );
    expect(
      component.formGroup.get('workAlternativePhone').validator
    ).toBeNull();
    expect(
      component.formGroup.get('noMobilePhoneNumberReason').validator
    ).toBeNull();
  });

  it('should make workAlternativePhone and noMobilePhoneNumberReason required when hasWorkMobilePhone is false', () => {
    component.hasWorkMobilePhone = false;
    component.ngOnInit();

    expect(component.formGroup.get('workMobilePhone').validator).toBeNull();
    expect(component.formGroup.get('workAlternativePhone').validator).toBe(
      UkRegistryValidators.allFieldsRequired
    );
    expect(
      component.formGroup.get('noMobilePhoneNumberReason').validator
    ).toEqual(Validators.required);
  });

  it('should fill workMobilePhone with values from user input', () => {
    const countryCode = 'UK (44)';
    const phoneNumber = '1234567890';
    const user = new User();
    user.workMobileCountryCode = countryCode;
    user.workMobilePhoneNumber = phoneNumber;
    component.user = user;
    component.ngOnInit();

    expect(component.formGroup.get('workMobilePhone').value).toEqual({
      countryCode,
      phoneNumber,
    });
  });

  it('should update formGroup values when workCountry changes', () => {
    const workPostCodeControl = component.formGroup.get('workPostCode');
    component.formGroup.patchValue({ workCountry: 'US' });
    expect(workPostCodeControl.validator).toBeNull();
    component.formGroup.patchValue({ workCountry: 'UK' });
    expect(workPostCodeControl.validator).toBe(Validators.required);
  });
});
