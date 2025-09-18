import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UpdateRecoveryEmailVerificationComponent } from './update-recovery-email-verification.component';
import { FormBuilder, FormGroupDirective } from '@angular/forms';
import { SharedModule } from '@registry-web/shared/shared.module';
import { TimerComponent } from '@user-management/recovery-methods-change/components/timer/timer.component';
import { provideMockStore } from '@ngrx/store/testing';

const formBuilder = new FormBuilder();

describe('UpdateRecoveryEmailVerificationComponent', () => {
  let component: UpdateRecoveryEmailVerificationComponent;
  let fixture: ComponentFixture<UpdateRecoveryEmailVerificationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [
        UpdateRecoveryEmailVerificationComponent,
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
    fixture = TestBed.createComponent(UpdateRecoveryEmailVerificationComponent);
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
      newRecoveryCountryCode: null,
      newRecoveryPhoneNumber: null,
      newRecoveryEmailAddress: 'new_recovery_email@test.com',
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
  //     component = new UpdateRecoveryEmailVerificationComponent();
  //   });
  //   expect(component).toBeDefined();
  // });
});
