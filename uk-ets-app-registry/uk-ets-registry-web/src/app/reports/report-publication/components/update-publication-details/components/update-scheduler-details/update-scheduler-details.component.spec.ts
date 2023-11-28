import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSchedulerDetailsComponent } from './update-scheduler-details.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { DisplayType } from '@report-publication/model';
import { ReportType } from '@reports/model';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ConnectFormDirective } from '@shared/connect-form.directive';

const formBuilder = new FormBuilder();
describe('UpdateSchedulerDetailsComponent', () => {
  let component: UpdateSchedulerDetailsComponent;
  let fixture: ComponentFixture<UpdateSchedulerDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [
        UpdateSchedulerDetailsComponent,
        FormGroupDirective,
        ScreenReaderPageAnnounceDirective,
        ConnectFormDirective,
      ],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateSchedulerDetailsComponent);
    component = fixture.componentInstance;
    component.sectionDetails = {
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
