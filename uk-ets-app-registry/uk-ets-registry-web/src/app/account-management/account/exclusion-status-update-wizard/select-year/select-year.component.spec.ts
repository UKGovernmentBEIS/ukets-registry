import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { UkRadioInputComponent } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio-input.component';
import { SelectYearComponent } from '../select-year';

const formBuilder = new FormBuilder();

describe('SelectYearComponent', () => {
  let component: SelectYearComponent;
  let fixture: ComponentFixture<SelectYearComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectYearComponent, UkRadioInputComponent],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [ReactiveFormsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectYearComponent);
    component = fixture.componentInstance;
    component.emissionEntries = [
      {
        compliantEntityId: 123,
        year: 2021,
        reportableEmissions: 100,
        lastUpdated: new Date(),
      },
      {
        compliantEntityId: 123,
        year: 2022,
        reportableEmissions: 'Excluded',
        lastUpdated: new Date(),
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render the header and options correctly', () => {
    const title = fixture.debugElement.queryAll(
      By.css('.govuk-fieldset__heading')
    )[0];
    expect(title.nativeElement.textContent.trim()).toContain(
      'Select the year to update the operator exclusion status'
    );
    const option1 = fixture.debugElement.queryAll(
      By.css('.govuk-label.govuk-radios__label')
    )[0];
    expect(option1.nativeElement.textContent.trim()).toContain('2021');
    const option2 = fixture.debugElement.queryAll(
      By.css('.govuk-label.govuk-radios__label')
    )[1];
    expect(option2.nativeElement.textContent.trim()).toContain('2022');
  });
});
