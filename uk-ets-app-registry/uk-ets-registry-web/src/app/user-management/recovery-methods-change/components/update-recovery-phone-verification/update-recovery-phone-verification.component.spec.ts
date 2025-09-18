import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UpdateRecoveryPhoneVerificationComponent } from './update-recovery-phone-verification.component';
import { FormBuilder, FormGroupDirective } from '@angular/forms';
import { SharedModule } from '@registry-web/shared/shared.module';
import { TimerComponent } from '@user-management/recovery-methods-change/components/timer/timer.component';
import { provideMockStore } from '@ngrx/store/testing';

const formBuilder = new FormBuilder();

describe('UpdateRecoveryPhoneVerificationComponent', () => {
  let component: UpdateRecoveryPhoneVerificationComponent;
  let fixture: ComponentFixture<UpdateRecoveryPhoneVerificationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [
        UpdateRecoveryPhoneVerificationComponent,
        FormGroupDirective,
        TimerComponent,
      ],
      providers: [
        { provide: FormBuilder, useValue: formBuilder },
        provideMockStore(),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateRecoveryPhoneVerificationComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.state = {
      originCaller: null,
      caller: {
        route: null,
      },
      recoveryCountryCode: null,
      recoveryPhoneNumber: null,
      workMobileCountryCode: null,
      workMobilePhoneNumber: null,
      recoveryEmailAddress: null,
      newRecoveryCountryCode: 'GR (30)',
      newRecoveryPhoneNumber: '6977777777',
      newRecoveryEmailAddress: null,
      recoveryPhoneExpiredAt: null,
      recoveryPhoneResendSuccess: false,
      recoveryEmailExpiredAt: null,
      recoveryEmailResendSuccess: false,
      recoveryPhoneNumberSuccess: false,
      recoveryEmailAddressSuccess: false,
    };
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // it('should instantiate', () => {
  //   let component;
  //   TestBed.runInInjectionContext(() => {
  //     component = new UpdateRecoveryPhoneVerificationComponent();
  //   });
  //   expect(component).toBeDefined();
  // });
});
