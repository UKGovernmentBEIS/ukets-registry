import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckYourAnswersComponent } from './check-your-answers.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DisplayType } from '@report-publication/model';
import { ReportType } from '@reports/model';
import { FormGroupDirective } from '@angular/forms';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { GdsDatePipe, GdsDateShortNoYearPipe } from '@shared/pipes';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CheckYourAnswersComponent,
        FormGroupDirective,
        ScreenReaderPageAnnounceDirective,
        ConnectFormDirective,
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [GdsDatePipe, GdsDateShortNoYearPipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    component = fixture.componentInstance;
    component.sectionDetails = {
      id: 1,
      displayOrder: 0,
      displayType: DisplayType.ONE_FILE_PER_YEAR,
      reportType: ReportType.R0001,
      summary: 'Summary',
      title: 'Title',
    };
    component.updatedDetails = {
      id: 1,
      displayOrder: 0,
      displayType: DisplayType.ONE_FILE_PER_YEAR,
      reportType: ReportType.R0001,
      summary: 'Summary',
      title: 'Title',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
