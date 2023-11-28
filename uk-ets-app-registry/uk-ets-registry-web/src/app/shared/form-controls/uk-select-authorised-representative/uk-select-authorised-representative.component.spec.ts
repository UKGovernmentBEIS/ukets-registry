import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { UkSelectAuthorisedRepresentativeComponent } from './uk-select-authorised-representative.component';

import { ReactiveFormsModule } from '@angular/forms';
import { ARSelectionType } from '@shared/model/account';
import { Component } from '@angular/core';
import {
  arFormGroupData,
  authorisedRepresentativesData,
} from '../../../../stories/test-data';
import { By } from '@angular/platform-browser';
import { ArDisplayNamePipe } from '@registry-web/shared/pipes/ar-display-name.pipe';

describe('UkSelectAuthorisedRepresentativeComponent', () => {
  const selectArFormControlName = 'selectAr';
  let testHost: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          UkSelectAuthorisedRepresentativeComponent,
          TestHostComponent,
          ArDisplayNamePipe,
        ],
        imports: [ReactiveFormsModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    // create TestHostComponent instead of DashboardHeroComponent
    fixture = TestBed.createComponent(TestHostComponent);
    testHost = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be invalid at first', () => {
    expect(testHost).toBeTruthy();
    expect(testHost.arFormGroup.get(selectArFormControlName).valid).toBeFalsy();
  });

  it('should be invalid when choosing selection type', () => {
    testHost.arFormGroup.setValue({
      selectAr: {
        selectionTypeRadio: ARSelectionType.FROM_LIST,
        listSelect: '',
        userIdInput: '',
      },
    });

    expect(testHost.arFormGroup.valid).toBeFalsy();
    expect(testHost.arFormGroup.get(selectArFormControlName).errors).toEqual({
      ArSelectionRequired: 'Please select the representative from the list.',
    });
  });

  it('should be valid when choosing ar from list', () => {
    testHost.arFormGroup.setValue({
      selectAr: {
        selectionTypeRadio: ARSelectionType.FROM_LIST,
        listSelect: '111-111-111-111',
        userIdInput: '',
      },
    });

    expect(testHost.arFormGroup.valid).toBeTruthy();
    expect(testHost.arFormGroup.get(selectArFormControlName).errors).toBeNull();
  });

  it('should be valid when entering user id', () => {
    testHost.arFormGroup.setValue({
      selectAr: {
        selectionTypeRadio: ARSelectionType.BY_ID,
        listSelect: '',
        userIdInput: '111-111-111-111',
      },
    });

    expect(testHost.arFormGroup.valid).toBeTruthy();
    expect(testHost.arFormGroup.get(selectArFormControlName).errors).toBeNull();
  });

  it('should render the radio choices inner form controls with accessible names', () => {
    // given
    const userIdInputId = 'selection-by-id';
    const verifyThatFormControlHasLabel = (formControlId) => {
      const formControl = fixture.debugElement.query(
        By.css(`#${formControlId}`)
      );
      expect(formControl).toBeTruthy();
      const ariaLabel = formControl.nativeElement.getAttribute('aria-label');
      expect(ariaLabel).toBeTruthy();
    };
    // then
    verifyThatFormControlHasLabel('selection-from-dropdown');
    verifyThatFormControlHasLabel(userIdInputId);

    // given
    testHost.showOnlyUserIdInput = true;
    // when
    fixture.detectChanges();
    // then
    const userIdInput = fixture.debugElement.query(By.css(`#${userIdInputId}`));
    expect(userIdInput).toBeTruthy();
    expect(userIdInput.nativeElement.getAttribute('aria-label')).toBeFalsy();
    const userIdLabel = fixture.debugElement.query(
      By.css(`label[for=${userIdInputId}]`)
    );
    expect(userIdLabel).toBeTruthy();
  });
});

@Component({
  selector: 'app-test-host-component',
  template: `
    <form [formGroup]="arFormGroup">
      <app-uk-select-authorised-representative
        formControlName="selectAr"
        [authorisedRepresentatives]="authorisedRepresentatives"
        [hint]="arHint"
        [showErrors]="true"
        [showOnlyUserIdInput]="showOnlyUserIdInput"
      >
      </app-uk-select-authorised-representative>
      <button class="govuk-button" data-module="govuk-button" type="submit">
        Continue
      </button>
    </form>
  `,
})
class TestHostComponent {
  arFormGroup = arFormGroupData();
  authorisedRepresentatives = authorisedRepresentativesData;
  arHint: 'Select one option';
  showOnlyUserIdInput = false;
}
