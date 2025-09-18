import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UpdateRecoveryEmailFormComponent } from './update-recovery-email-form.component';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { SharedModule } from '@registry-web/shared/shared.module';

const formBuilder = new FormBuilder();

describe('UpdateRecoveryEmailFormComponent', () => {
  let component: UpdateRecoveryEmailFormComponent;
  let fixture: ComponentFixture<UpdateRecoveryEmailFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule],
      declarations: [UpdateRecoveryEmailFormComponent, FormGroupDirective],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateRecoveryEmailFormComponent);
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
      newRecoveryEmailAddress: null,
    };
    fixture.detectChanges();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    let component;
    TestBed.runInInjectionContext(() => {
      component = new UpdateRecoveryEmailFormComponent();
    });
    expect(component).toBeDefined();
  });
});
