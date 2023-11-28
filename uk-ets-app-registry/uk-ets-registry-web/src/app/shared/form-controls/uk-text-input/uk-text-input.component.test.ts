import { UkTextInputComponent, ENTER_TEXT } from './uk-text-input.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

describe('UkTextInputComponent', () => {
  let component: UkTextInputComponent;
  let fixture: ComponentFixture<UkTextInputComponent>;

  function getHintSpan(): DebugElement {
    return fixture.debugElement.query(By.css(`span.govuk-hint`));
  }

  function getLabel(): DebugElement {
    return fixture.debugElement.query(By.css(`label.govuk-label`));
  }

  function getInput(): DebugElement {
    return fixture.debugElement.query(By.css(`input.govuk-input`));
  }

  function getRenderedHintText(): string {
    return getHintSpan().nativeElement.textContent.trim();
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule],
      declarations: [UkTextInputComponent],
    });

    fixture = TestBed.createComponent(UkTextInputComponent);
    component = fixture.componentInstance;
    component.form = new FormGroup({});
    component.id = 'testField';
    fixture.detectChanges();
  });

  test(`The default initial state of the uk text input is
         the empty string`, () => {
    expect(component.formState).toBe('');
    expect(component.formControl.value).toBe('');
    expect(component.form.controls['testField']).toBeTruthy();
    expect(
      fixture.debugElement.query(By.css('input[type="text"]')).nativeElement
        .value
    ).toBe('');
  });

  test(`The default hint text is the ${ENTER_TEXT}`, () => {
    expect(component.hint).toBe(ENTER_TEXT);
    expect(getRenderedHintText()).toBe(ENTER_TEXT);
  });

  test('The hint text is rendered inside the content of a span with id equals with "{id}-hint" ', () => {
    const span = getHintSpan();
    expect(span.attributes['id']).toBe(`${component.id}-hint`);
  });

  test('The label for attribute is the component id', () => {
    expect(getLabel().attributes['for']).toBe(component.id);
  });

  test(`The text input should have an id attribute equals with the component id`, () => {
    expect(getInput().attributes['id']).toBe(component.id);
  });

  test(`The text input should have a name attribute equals with the component id`, () => {
    expect(getInput().attributes['id']).toBe(component.id);
  });

  test(`The text input should have an aria-describedby attribute equals with {component.id}-hint`, () => {
    expect(getInput().attributes['aria-describedby']).toBe(
      `${component.id}-hint`
    );
  });

  test('The text input should have a maxlength attribute equals with the component maxlegth property', () => {
    expect(+getInput().attributes['maxlength']).toBe(component.maxlength);
  });
});
