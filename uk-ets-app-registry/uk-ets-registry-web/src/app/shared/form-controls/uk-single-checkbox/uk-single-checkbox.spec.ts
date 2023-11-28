import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UkSingleCheckboxComponent } from '@shared/form-controls/uk-single-checkbox/uk-single-checkbox.component';
import { By } from '@angular/platform-browser';
import { Component, DebugElement } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

const TEST_LABEL = 'Test label checkbox';
const TEST_KEY = 'test-key-checkbox';
const TEST_ERROR_MESSAGE = 'Invalid value';
describe('UkSingleCheckboxComponent', () => {
  let testHost: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  function getCheckboxInput() {
    return fixture.debugElement.query(By.css(`input.govuk-checkboxes__input`))
      .nativeElement;
  }

  function getCheckboxLabel() {
    return fixture.debugElement.query(By.css(`label.govuk-checkboxes__label`))
      .nativeElement;
  }

  function getCheckboxElement() {
    return fixture.debugElement.query(By.css(`input`)).nativeElement;
  }

  function getSubmitButton() {
    return fixture.debugElement.query(By.css(`button.govuk-button`))
      .nativeElement;
  }

  function getErrorSpan() {
    return fixture.debugElement.query(By.css(`span.govuk-error-message`))
      ?.nativeElement;
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [
        TestHostComponent,
        UkSingleCheckboxComponent,
        UkFormComponent,
      ],
    });

    fixture = TestBed.createComponent(TestHostComponent);
    testHost = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should not show error message initially', () => {
    expect(
      testHost.formGroup.get('innerGroup').get('checkbox').valid
    ).toBeFalsy();

    const error: DebugElement = getErrorSpan();

    expect(error).toBeUndefined();
  });

  it('should render key and label', () => {
    const checkboxLabel = getCheckboxLabel();

    const checkboxInput = getCheckboxInput();

    expect(checkboxLabel.textContent.trim()).toBe(TEST_LABEL);
    expect(checkboxInput.id).toBe(TEST_KEY);
  });

  it('should render error message when invalid', () => {
    getSubmitButton().click();

    fixture.detectChanges();

    const error = getErrorSpan();

    expect(error.textContent.trim()).toBe('Error:Invalid value');
  });

  it('should submit when valid', () => {
    getCheckboxElement().click();

    fixture.detectChanges();

    getSubmitButton().click();

    fixture.detectChanges();

    const error = getErrorSpan();

    expect(error).toBeUndefined();
    expect(testHost.formGroup.get('innerGroup').valid).toBeTruthy();
  });
});

@Component({
  selector: 'app-test-host-component',
  template: `
    <form [formGroup]="formGroup" (ngSubmit)="onSubmit()">
      <div formGroupName="innerGroup">
        <app-uk-single-checkbox
          [key]="key"
          [label]="label"
          controlName="innerGroup.checkbox"
          [validationErrorMessage]="validationErrorMessage.checkbox"
        >
        </app-uk-single-checkbox>
        <button class="govuk-button" data-module="govuk-button" type="submit">
          Continue
        </button>
      </div>
    </form>
  `,
})
class TestHostComponent extends UkFormComponent {
  key = TEST_KEY;
  label = TEST_LABEL;
  message = TEST_ERROR_MESSAGE;

  constructor(protected formBuilder: FormBuilder) {
    super();
  }
  protected doSubmit() {
    return;
  }

  protected getFormModel(): any {
    return {
      innerGroup: this.formBuilder.group({
        checkbox: ['', Validators.required],
      }),
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      checkbox: {
        required: TEST_ERROR_MESSAGE,
      },
    };
  }
}
