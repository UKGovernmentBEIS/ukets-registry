import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';

import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { StandardReportsFiltersComponent } from '@reports/components';
import { provideMockStore } from '@ngrx/store/testing';
import { By } from '@angular/platform-browser';
import {
  NgbTypeaheadModule,
  NgbDatepickerModule,
} from '@ng-bootstrap/ng-bootstrap';
import { ReportType, StandardReport } from '@registry-web/reports/model';
import { of } from 'rxjs';
import { first, tap } from 'rxjs/operators';
import {
  UkProtoFormDatePickerComponent,
  UkProtoFormSelectComponent,
} from '@registry-web/shared/form-controls/uk-proto-form-controls';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

const selectedReport: StandardReport = {
  type: ReportType.R0001,
  label: 'Accounts with no ARs and no AR nominations',
};

describe('StandardReportComponent', () => {
  let component: StandardReportsFiltersComponent;
  let fixture: ComponentFixture<StandardReportsFiltersComponent>;
  let store: Store;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        UkProtoFormSelectComponent,
        UkProtoFormDatePickerComponent,
        StandardReportsFiltersComponent,
      ],
      imports: [
        NgbTypeaheadModule,
        NgbDatepickerModule,
        ReactiveFormsModule,
        FormsModule,
        StoreModule.forRoot({}),
        RouterTestingModule,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StandardReportsFiltersComponent);
    component = fixture.componentInstance;
    component.selectedReport$ = of(selectedReport);
    store = TestBed.inject(Store);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('Emit type and criteria required for report creation', async () => {
    component.selectedReport$
      .pipe(
        first(),
        tap(() => {
          let emmitedCriteria;
          component.reportCreation.subscribe((value) => {
            emmitedCriteria = value;
          });
          fixture.debugElement
            .query(By.css('button.govuk-button'))
            .nativeElement.click();
          expect(emmitedCriteria).toBeTruthy();
        })
      )
      .subscribe();
  });
});
