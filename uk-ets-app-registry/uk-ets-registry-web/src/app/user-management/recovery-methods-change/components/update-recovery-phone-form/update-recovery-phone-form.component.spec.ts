import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UpdateRecoveryPhoneFormComponent } from './update-recovery-phone-form.component';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { SharedModule } from '@registry-web/shared/shared.module';
import { By } from '@angular/platform-browser';

const formBuilder = new FormBuilder();

describe('UpdateRecoveryPhoneFormComponent', () => {
  let component: UpdateRecoveryPhoneFormComponent;
  let fixture: ComponentFixture<UpdateRecoveryPhoneFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule],
      declarations: [UpdateRecoveryPhoneFormComponent, FormGroupDirective],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateRecoveryPhoneFormComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.state = {
      originCaller: null,
      caller: {
        route: null,
      },
      recoveryCountryCode: null,
      recoveryPhoneNumber: null,
      workMobileCountryCode: 'GR (30)',
      workMobilePhoneNumber: '6977777777',
      recoveryEmailAddress: null,
      newRecoveryCountryCode: null,
      newRecoveryPhoneNumber: null,
      newRecoveryEmailAddress: null,
    };
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    let component;
    TestBed.runInInjectionContext(() => {
      component = new UpdateRecoveryPhoneFormComponent();
    });
    expect(component).toBeDefined();
  });

  it('should show isTheSameAsWorkMobile checkbox if workMobilePhoneNumber defined', () => {
    fixture.detectChanges();

    const checkbox = fixture.debugElement.queryAll(
      By.css('#same-phone-checkbox')
    );
    expect(checkbox.length).toBe(1);
    expect(checkbox[0]).toBeDefined();

    const label = fixture.debugElement.queryAll(
      By.css('[for="same-phone-checkbox"]')
    );
    expect(label[0].nativeElement.textContent.trim()).toBe(
      'My recovery phone number is the same as my work mobile number'
    );
  });

  it('should not show isTheSameAsWorkMobile checkbox if workMobilePhoneNumber not defined', () => {
    fixture.componentInstance.state.workMobileCountryCode = null;
    fixture.componentInstance.state.workMobilePhoneNumber = null;

    fixture.detectChanges();

    const checkbox = fixture.debugElement.query(By.css('#same-phone-checkbox'));
    expect(checkbox).toBeFalsy();
  });

  it('should fill phone number fields on isTheSameAsWorkMobile checkbox click', () => {
    fixture.detectChanges();

    const checkbox = fixture.debugElement.query(By.css('#same-phone-checkbox'));
    expect(checkbox).toBeDefined();

    checkbox.nativeElement.click();
    expect(
      fixture.componentInstance.formGroup.get('newRecoveryPhone').value
    ).toStrictEqual({
      countryCode: fixture.componentInstance.state.workMobileCountryCode,
      phoneNumber: fixture.componentInstance.state.workMobilePhoneNumber,
    });
  });

  it('should have isTheSameAsWorkMobile checkbox checked when work mobile and recovery number are the same', () => {
    fixture.componentInstance.state.recoveryCountryCode =
      fixture.componentInstance.state.workMobileCountryCode;
    fixture.componentInstance.state.recoveryPhoneNumber =
      fixture.componentInstance.state.workMobilePhoneNumber;

    fixture.detectChanges();

    expect(
      fixture.componentInstance.formGroup.get('isTheSameAsWorkMobile').value
    ).toBe(true);

    fixture.componentInstance.formGroup
      .get('newRecoveryPhone')
      .setValue({ countryCode: 'GR (30)', phoneNumber: '6988889999' });
    expect(
      fixture.componentInstance.formGroup.get('isTheSameAsWorkMobile').value
    ).toBe(false);
  });
});
