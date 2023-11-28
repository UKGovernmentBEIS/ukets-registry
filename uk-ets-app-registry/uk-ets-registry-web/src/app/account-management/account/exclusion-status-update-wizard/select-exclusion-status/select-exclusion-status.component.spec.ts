import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { UkRadioInputComponent } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio-input.component';
import { SelectExclusionStatusComponent } from '../select-exclusion-status';

const formBuilder = new FormBuilder();

describe('SelectExclusionStatusComponent', () => {
  let component: SelectExclusionStatusComponent;
  let fixture: ComponentFixture<SelectExclusionStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectExclusionStatusComponent, UkRadioInputComponent],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [ReactiveFormsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectExclusionStatusComponent);
    component = fixture.componentInstance;
    component.emissionEntries = [
      {
        compliantEntityId: 123,
        year: 2021,
        reportableEmissions: 100,
        lastUpdated: new Date(),
      },
      {
        compliantEntityId: 345,
        year: 2022,
        reportableEmissions: 'Excluded',
        lastUpdated: new Date(),
      },
    ];
    component.year = 2021;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render the sub heading and options correctly', () => {
    const title = fixture.debugElement.queryAll(By.css('.govuk-heading-m'))[0];
    expect(title.nativeElement.textContent.trim()).toEqual('Year 2021');
    const option1 = fixture.debugElement.queryAll(
      By.css('.govuk-label.govuk-radios__label')
    )[0];
    expect(option1.nativeElement.textContent.trim()).toContain('Yes');
    const option2 = fixture.debugElement.queryAll(
      By.css('.govuk-label.govuk-radios__label')
    )[1];
    expect(option2.nativeElement.textContent.trim()).toContain('No');
  });
});
