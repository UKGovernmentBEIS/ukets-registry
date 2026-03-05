import { TestBed, waitForAsync } from '@angular/core/testing';
import { BulkActionsButtonGroupComponent } from './bulk-actions-button-group.component';
import * as fixtureTestHelper from 'src/testing/helpers/from-fixture.test.helper';

describe('BulkActionsButtonGroupComponent', () => {
  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [BulkActionsButtonGroupComponent],
    }).compileComponents();
  }));

  test('the component is created', () => {
    const fixture = TestBed.createComponent(BulkActionsButtonGroupComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  test('Setting displayable to false the claim and assign buttons are not rendered', () => {
    const fixture = TestBed.createComponent(BulkActionsButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = false;
    fixture.detectChanges();
    expect(fixtureTestHelper.getElement(fixture, '.claim-button')).toBeFalsy();
    expect(fixtureTestHelper.getElement(fixture, '.assign-button')).toBeFalsy();
  });

  test('Setting displayable to true the claim and assign buttons are rendered', () => {
    const fixture = TestBed.createComponent(BulkActionsButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = true;
    fixture.detectChanges();
    expect(fixtureTestHelper.getElement(fixture, '.claim-button')).toBeTruthy();
    expect(
      fixtureTestHelper.getElement(fixture, '.assign-button')
    ).toBeTruthy();
  });

  test('Setting displayable to true and disabled to true the claim and assign buttons are rendered disabled', () => {
    const fixture = TestBed.createComponent(BulkActionsButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = true;
    component.disabled = true;
    fixture.detectChanges();
    expect(
      fixtureTestHelper.getElement(fixture, '.claim-button[disabled]')
    ).toBeTruthy();
    expect(
      fixtureTestHelper.getElement(fixture, '.assign-button[disabled]')
    ).toBeTruthy();
  });

  test('Setting displayable to true and disabled to true the claim and assign buttons are rendered enabled', () => {
    const fixture = TestBed.createComponent(BulkActionsButtonGroupComponent);
    const component = fixture.componentInstance;
    component.displayable = true;
    component.disabled = false;
    fixture.detectChanges();
    expect(
      fixtureTestHelper.getElement(fixture, '.claim-button:not([disabled])')
    ).toBeTruthy();
    expect(
      fixtureTestHelper.getElement(fixture, '.assign-button:not([disabled])')
    ).toBeTruthy();
  });
});
