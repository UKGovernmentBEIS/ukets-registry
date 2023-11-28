import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Component } from '@angular/core';
import { UkDate } from '@shared/model/uk-date';
import { SharedModule } from '@registry-web/shared/shared.module';

describe('UkDateControlComponent', () => {
  let testHost: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule],
      declarations: [TestHostComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    testHost = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be invalid at first', () => {
    expect(testHost).toBeTruthy();
    expect(testHost.ukDateFormGroup.get('birthDate').valid).toBeFalsy();
  });

  it('should be valid when choosing a valid date', () => {
    const date = new Date();
    date.setFullYear(date.getFullYear() - 11);
    const year = date.getFullYear();
    const validDate: UkDate = {
      day: '21',
      month: '11',
      year: year.toString(),
    };
    testHost.ukDateFormGroup.setValue({ birthDate: validDate });
    expect(testHost.ukDateFormGroup.valid).toBeTruthy();
    expect(testHost.ukDateFormGroup.get('birthDate').errors).toBeNull();
  });

  it('should be invalid with missingField error when year is missing ', () => {
    const dateWithMissingYear: UkDate = {
      day: '21',
      month: '11',
      year: null,
    };
    testHost.ukDateFormGroup.setValue({ birthDate: dateWithMissingYear });
    expect(testHost.ukDateFormGroup.invalid).toBeTruthy();
    const errors = testHost.ukDateFormGroup.get('birthDate').errors;
    expect(errors).toStrictEqual({
      missingField: 'Enter a complete date',
    });
    // Tests the message that is displayed
    Object.keys(errors).forEach((r) => {
      expect(errors[r]).toEqual('Enter a complete date');
    });
  });

  it('should be invalid with invalidInput error when month cannot be converted to number', () => {
    const dateWithNoNumericMonth: UkDate = {
      day: '21',
      month: '2s',
      year: '2000',
    };
    testHost.ukDateFormGroup.setValue({ birthDate: dateWithNoNumericMonth });
    expect(testHost.ukDateFormGroup.invalid).toBeTruthy();
    const errors = testHost.ukDateFormGroup.get('birthDate').errors;
    expect(errors).toStrictEqual({
      invalidInput: 'Enter a valid date',
    });
    // Tests the message that is displayed
    Object.keys(errors).forEach((r) => {
      expect(errors[r]).toEqual('Enter a valid date');
    });
  });

  it('should be invalid with invalidDay error when day is invalid', () => {
    const dateWithInvalidDay: UkDate = {
      day: '41',
      month: '11',
      year: '2000',
    };
    testHost.ukDateFormGroup.setValue({ birthDate: dateWithInvalidDay });
    expect(testHost.ukDateFormGroup.invalid).toBeTruthy();
    const errors = testHost.ukDateFormGroup.get('birthDate').errors;
    expect(errors).toStrictEqual({
      invalidDay: 'Enter a valid day',
    });
    // Tests the message that is displayed
    Object.keys(errors).forEach((r) => {
      expect(errors[r]).toEqual('Enter a valid day');
    });
  });

  it('should be invalid with invalidMonth error when month is invalid', () => {
    const dateWithMissingYear: UkDate = {
      day: '21',
      month: '41',
      year: '2000',
    };
    testHost.ukDateFormGroup.setValue({ birthDate: dateWithMissingYear });
    expect(testHost.ukDateFormGroup.invalid).toBeTruthy();
    const errors = testHost.ukDateFormGroup.get('birthDate').errors;
    expect(errors).toStrictEqual({
      invalidMonth: 'Enter a valid month',
    });
    // Tests the message that is displayed
    Object.keys(errors).forEach((r) => {
      expect(errors[r]).toEqual('Enter a valid month');
    });
  });

  it('should be invalid when maxAge is set', () => {
    const maxAgeInvalidDate: UkDate = {
      day: '21',
      month: '11',
      year: '1950',
    };
    testHost.ukDateFormGroup.setValue({ birthDate: maxAgeInvalidDate });
    expect(testHost.ukDateFormGroup.invalid).toBeTruthy();
    expect(testHost.ukDateFormGroup.get('birthDate').errors).toStrictEqual({
      tooOld: true,
    });
  });

  it('should be invalid when minAge is set', () => {
    const minAgeInvalidDate: UkDate = {
      day: '21',
      month: '11',
      year: '2019',
    };
    testHost.ukDateFormGroup.setValue({ birthDate: minAgeInvalidDate });
    expect(testHost.ukDateFormGroup.invalid).toBeTruthy();
    expect(testHost.ukDateFormGroup.get('birthDate').errors).toStrictEqual({
      tooYoung: true,
    });
  });
});

@Component({
  selector: 'app-test-host-component',
  template: `
    <form [formGroup]="ukDateFormGroup">
      <app-uk-date-control
        [hint]="'For example, 31 3 1980'"
        [showErrors]="true"
        formControlName="birthDate"
        [minAge]="10"
        [maxAge]="20"
      ></app-uk-date-control>
      <button class="govuk-button" data-module="govuk-button" type="submit">
        Continue
      </button>
    </form>
  `,
})
class TestHostComponent {
  ukDateFormGroup = new FormGroup(
    {
      birthDate: new FormControl(''),
    },
    { updateOn: 'submit' }
  );
}
