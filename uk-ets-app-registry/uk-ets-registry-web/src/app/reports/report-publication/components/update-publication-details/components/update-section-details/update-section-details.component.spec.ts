import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSectionDetailsComponent } from './update-section-details.component';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { ReportType } from '@reports/model';
import { DisplayType } from '@report-publication/model';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

const formBuilder = new FormBuilder();
describe('UpdateSectionDetailsComponent', () => {
  let component: UpdateSectionDetailsComponent;
  let fixture: ComponentFixture<UpdateSectionDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [
        UpdateSectionDetailsComponent,
        ConnectFormDirective,
        FormGroupDirective,
        ScreenReaderPageAnnounceDirective,
      ],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateSectionDetailsComponent);
    component = fixture.componentInstance;
    component.sectionDetails = {
      displayOrder: 0,
      displayType: DisplayType.ONE_PER_YEAR,
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
