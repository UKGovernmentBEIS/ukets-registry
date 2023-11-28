import { FormsModule, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import {
  UkSelectInputComponent,
  SELECT_FROM_LIST,
} from 'src/app/shared/form-controls/uk-select-input/uk-select-input.component';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

describe('SearchTasksFormComponent', () => {
  const formGroup = new FormGroup({});
  let component: UkSelectInputComponent;
  let fixture: ComponentFixture<UkSelectInputComponent>;

  function getHintSpan(): DebugElement {
    return fixture.debugElement.query(By.css(`span.govuk-hint`));
  }

  function getLabel(): DebugElement {
    return fixture.debugElement.query(By.css(`label.govuk-label`));
  }

  function getInput(): DebugElement {
    return fixture.debugElement.query(By.css(`select.govuk-select`));
  }

  function getRenderedHintText(): string {
    return getHintSpan().nativeElement.textContent.trim();
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule],
      declarations: [UkSelectInputComponent],
    });

    fixture = TestBed.createComponent(UkSelectInputComponent);
    component = fixture.componentInstance;
    component.id = 'testField';
    component.form = formGroup;
    component.options = [
      {
        label: 'label1',
        value: 'value1',
      },
      {
        label: 'label2',
        value: 'value2',
      },
      {
        label: 'label2',
        value: 'value2',
      },
    ];
    fixture.detectChanges();
  });

  test(`The default initial state of the uk select input is null`, () => {
    expect(component.formState).toBeUndefined();
    expect(component.form.controls[component.id].value).toBeNull();
  });

  test(`The default hint text is the "${SELECT_FROM_LIST}"`, () => {
    expect(component.hint).toBe(SELECT_FROM_LIST);
    expect(getRenderedHintText()).toBe(SELECT_FROM_LIST);
  });

  test('The hint text is rendered inside the content of a span with id equals with "{id}-hint" ', () => {
    expect(getHintSpan().attributes['id']).toBe(`${component.id}-hint`);
  });

  test('The label for attribute is the component id', () => {
    expect(getLabel().attributes['for']).toBe(component.id);
  });

  test(`The select should have an id attribute equals with the component id`, () => {
    expect(getInput().attributes['id']).toBe(component.id);
  });

  test(`The select should have a name attribute equals with the component id`, () => {
    expect(getInput().attributes['id']).toBe(component.id);
  });

  test(`The select should have an aria-describedby attribute equals with {component.id}-hint`, () => {
    expect(getInput().attributes['aria-describedby']).toBe(
      `${component.id}-hint`
    );
  });
});
