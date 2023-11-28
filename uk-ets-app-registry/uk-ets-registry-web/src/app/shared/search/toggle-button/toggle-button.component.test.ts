import { TestBed, waitForAsync } from '@angular/core/testing';
import { ToggleButtonComponent } from './toggle-button.component';
import * as fixtureTestHelper from 'src/testing/helpers/from-fixture.test.helper';
import { By } from '@angular/platform-browser';
import { Component } from '@angular/core';

describe('ShowHideToggleButtonComponent', () => {
  @Component({
    selector: 'app-test-component',
    template: `
      <app-show-hide-toggle-button
        trueStateLabel="Show"
        falseStateLabel="Hide"
        [condition]="condition"
        [displayable]="true"
        (toggle)="toggle($event)"
      >
      </app-show-hide-toggle-button>
    `,
  })
  class TestComponent {
    condition = true;

    toggle(condition: boolean) {
      this.condition = condition;
    }
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ToggleButtonComponent, TestComponent],
      }).compileComponents();
    })
  );

  test('the component is created', () => {
    const fixture = TestBed.createComponent(ToggleButtonComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  test('the button is not rendered if the displayable input is false', () => {
    const fixture = TestBed.createComponent(ToggleButtonComponent);
    const component = fixture.componentInstance;
    component.displayable = false;
    fixture.detectChanges();
    const button = fixtureTestHelper.getElement(fixture, 'button');
    expect(button).toBeFalsy();
  });

  test('The text label of button changes accordingly to the condition input', () => {
    const fixture = TestBed.createComponent(ToggleButtonComponent);
    const component = fixture.componentInstance;
    component.trueStateLabel = 'labelForTrue';
    component.falseStateLabel = 'labelForFalse';
    component.condition = true;
    component.displayable = true;
    fixture.detectChanges();

    let button = fixtureTestHelper.getElement(fixture, 'button');
    expect(button.textContent.trim()).toBe(component.trueStateLabel);

    component.condition = false;
    fixture.detectChanges();
    button = fixtureTestHelper.getElement(fixture, 'button');
    expect(button.textContent.trim()).toBe(component.falseStateLabel);
  });

  test('The toggle button emits the opposite value of condition field on click', () => {
    let state;
    const fixture = TestBed.createComponent(ToggleButtonComponent);
    const component = fixture.componentInstance;
    component.toggle.subscribe((value) => (state = value));

    component.condition = true;
    component.displayable = true;
    fixture.detectChanges();
    fixtureTestHelper.click(fixture, 'button');
    expect(state).toBe(false);

    component.condition = false;
    fixture.detectChanges();
    fixtureTestHelper.click(fixture, 'button');
    expect(state).toBe(true);
  });

  test('The aria-expanded attribute toggles its value correctly on button clicks', () => {
    const fixture = TestBed.createComponent(TestComponent);

    const verifyAriaExpanded = (expanded) => {
      fixture.detectChanges();
      expect(
        fixture.debugElement.query(By.css(`button`)).attributes['aria-expanded']
      ).toEqual(String(expanded));
    };

    verifyAriaExpanded(false);

    fixtureTestHelper.click(fixture, 'button');

    verifyAriaExpanded(true);

    fixtureTestHelper.click(fixture, 'button');

    verifyAriaExpanded(false);
  });
});
