import { TestBed, waitForAsync } from '@angular/core/testing';
import { ClaimAssignButtonGroupComponent } from './claim-assign-button-group.component';
import { By } from '@angular/platform-browser';
import * as fixtureTestHelper from 'src/testing/helpers/from-fixture.test.helper';

describe('ClaimAssignButtonGroupComponent', () => {
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ClaimAssignButtonGroupComponent],
      }).compileComponents();
    })
  );

  test('the component is created', () => {
    const fixture = TestBed.createComponent(ClaimAssignButtonGroupComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  test('Setting displayable to false the claim and assign buttons are not rendered', () => {
    const fixture = TestBed.createComponent(ClaimAssignButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = false;
    fixture.detectChanges();
    const debugElement = fixture.debugElement;
    expect(fixtureTestHelper.getElement(fixture, '.claim-button')).toBeFalsy();
    expect(fixtureTestHelper.getElement(fixture, '.assign-button')).toBeFalsy();
  });

  test('Setting displayable to true the claim and assign buttons are rendered', () => {
    const fixture = TestBed.createComponent(ClaimAssignButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = true;
    fixture.detectChanges();
    const debugElement = fixture.debugElement;
    expect(fixtureTestHelper.getElement(fixture, '.claim-button')).toBeTruthy();
    expect(
      fixtureTestHelper.getElement(fixture, '.assign-button')
    ).toBeTruthy();
  });

  test('Setting displayable to true and disabled to true the claim and assign buttons are rendered disabled', () => {
    const fixture = TestBed.createComponent(ClaimAssignButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = true;
    component.disabled = true;
    fixture.detectChanges();
    const debugElement = fixture.debugElement;
    expect(
      fixtureTestHelper.getElement(fixture, '.claim-button[disabled]')
    ).toBeTruthy();
    expect(
      fixtureTestHelper.getElement(fixture, '.assign-button[disabled]')
    ).toBeTruthy();
  });

  test('Setting displayable to true and disabled to true the claim and assign buttons are rendered enabled', () => {
    const fixture = TestBed.createComponent(ClaimAssignButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = true;
    component.disabled = false;
    fixture.detectChanges();
    const debugElement = fixture.debugElement;
    expect(
      fixtureTestHelper.getElement(fixture, '.claim-button:not([disabled])')
    ).toBeTruthy();
    expect(
      fixtureTestHelper.getElement(fixture, '.assign-button:not([disabled])')
    ).toBeTruthy();
  });
});
