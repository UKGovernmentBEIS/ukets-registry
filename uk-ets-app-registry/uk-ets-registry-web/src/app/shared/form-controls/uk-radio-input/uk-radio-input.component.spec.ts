import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Component, DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('UkRadioInputComponent', () => {
  let component: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  function getErrorSpan() {
    return fixture.debugElement.query(By.css(`p.govuk-error-message`))
      ?.nativeElement;
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UkRadioInputComponent, UkFormComponent, TestHostComponent],
      imports: [ReactiveFormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show error message initially', () => {
    expect(
      component.formGroup.get('innerGroup').get('testRadioInput').valid
    ).toBeFalsy();

    const error: DebugElement = getErrorSpan();

    expect(error).toBeUndefined();
  });
});

@Component({
  selector: 'app-test-host-component',
  template: `
    <form [formGroup]="formGroup" (ngSubmit)="onSubmit()">
      <div formGroupName="innerGroup">
        <app-uk-radio-input
          controlName="innerGroup.testRadioInput"
          [selectedType]="null"
          [formRadioGroupInfo]="formRadioGroupInfo"
          [validationErrorMessage]="validationErrorMessage.testRadioInput"
        ></app-uk-radio-input>
        <button class="govuk-button" data-module="govuk-button" type="submit">
          Continue
        </button>
      </div>
    </form>
  `,
})
class TestHostComponent extends UkFormComponent {
  formRadioGroupInfo = {
    radioGroupHeading: 'Choose from the list below',
    radioGroupHeadingCaption: 'Heading caption',
    radioGroupHint: '',
    key: 'testRadioInput',
    options: [
      {
        label: 'Label 1',
        value: 'Value 1',
        enabled: true,
        showExtraInputField: false,
      },
    ],
  };

  constructor(protected formBuilder: FormBuilder) {
    super();
  }
  protected doSubmit() {
    return;
  }

  protected getFormModel(): any {
    return {
      innerGroup: this.formBuilder.group({
        testRadioInput: ['', Validators.required],
      }),
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      testRadioInput: {
        required: 'The field is required',
      },
    };
  }
}
