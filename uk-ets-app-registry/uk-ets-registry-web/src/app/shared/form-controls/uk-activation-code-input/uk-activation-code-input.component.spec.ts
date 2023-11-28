import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UkActivationCodeInputComponent } from './uk-activation-code-input.component';
import { provideMockStore } from '@ngrx/store/testing';
import {
  FormBuilder,
  FormGroupDirective,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

describe('UkActivationCodeInputComponent', () => {
  let component: UkActivationCodeInputComponent;
  let fixture: ComponentFixture<UkActivationCodeInputComponent>;
  const formBuilder: FormBuilder = new FormBuilder();
  const formDirective: FormGroupDirective = new FormGroupDirective([], []);

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UkActivationCodeInputComponent],
      imports: [ReactiveFormsModule, FormsModule],
      providers: [
        provideMockStore(),
        { provide: FormBuilder, useValue: formBuilder },
        { provide: FormGroupDirective, useValue: formDirective }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UkActivationCodeInputComponent);
    formDirective.form = formBuilder.group(
      {
        activationCodeInput1: ['', Validators.required],
        activationCodeInput2: ['', Validators.required],
        activationCodeInput3: ['', Validators.required],
        activationCodeInput4: ['', Validators.required],
        activationCodeInput5: ['', Validators.required]
      },
      { updateOn: 'submit' }
    );
    component = fixture.componentInstance;
    fixture.componentInstance.maxlength = 4;
    fixture.componentInstance.titleToDisplay = '';
    fixture.componentInstance.formControlInput1 = 'activationCodeInput1';
    fixture.componentInstance.formControlInput2 = 'activationCodeInput2';
    fixture.componentInstance.formControlInput3 = 'activationCodeInput3';
    fixture.componentInstance.formControlInput4 = 'activationCodeInput4';
    fixture.componentInstance.formControlInput5 = 'activationCodeInput5';
    fixture.detectChanges();
  });

  it('should create', () => {
    const component: UkActivationCodeInputComponent = new UkActivationCodeInputComponent(
      formDirective
    );
    expect(component).toBeTruthy();
  });
});
