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
import { MockStore, provideMockStore } from '@ngrx/store/testing';

const formBuilder = new FormBuilder();

describe('UpdateSectionDetailsComponent', () => {
  let component: UpdateSectionDetailsComponent;
  let fixture: ComponentFixture<UpdateSectionDetailsComponent>;
  let store: MockStore;
  const initialState = {
    id: 2,
    reportType: 'R0001',
    title: 'A title',
    summary: 'A summary',
    displayType: 'ONE_FILE',
    reportPublicationFrequency: 'DAILY',
    publicationStart: '',
    publicationTime: '',
    generationDate: '',
    generationTime: '',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [
        UpdateSectionDetailsComponent,
        ConnectFormDirective,
        FormGroupDirective,
        ScreenReaderPageAnnounceDirective,
      ],
      providers: [
        { provide: FormBuilder, useValue: formBuilder },
        provideMockStore({ initialState }),
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateSectionDetailsComponent);
    store = TestBed.inject(MockStore);
    component = fixture.componentInstance;
    component.sectionDetails = {
      id: 2,
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
