import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { provideMockStore } from '@ngrx/store/testing';
import { GdsDatePipe, GdsDateShortNoYearPipe, GdsDateTimePipe } from "@registry-web/shared/pipes";
import { SortService } from '@registry-web/shared/search/sort/sort.service';
import { SortableColumnDirective } from '@registry-web/shared/search/sort/sortable-column.directive';
import { SortableTableDirective } from '@registry-web/shared/search/sort/sortable-table.directive';
import { SummaryListComponent } from '@registry-web/shared/summary-list';

import { SectionDetailsComponent } from './section-details.component';
import { DisplayType, PublicationFrequency } from '@report-publication/model';
import { ReportType } from '@reports/model';

describe('SectionDetailsComponent', () => {
  let component: SectionDetailsComponent;
  let fixture: ComponentFixture<SectionDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      declarations: [
        SectionDetailsComponent,
        SummaryListComponent,
        SortableTableDirective,
        SortableColumnDirective,
        GdsDateTimePipe,
        GdsDatePipe,
      ],
      providers: [
        SortService,
        GdsDatePipe,
        GdsDateShortNoYearPipe,
        { provide: APP_BASE_HREF, useValue: '/' },
        provideMockStore(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SectionDetailsComponent);
    component = fixture.componentInstance;
    component.section = {
      id: 1,
      displayOrder: 1,
      title: 'Title1',
      summary: 'Summary1',
      reportType: ReportType.R0001,
      displayType: DisplayType.ONE_FILE_PER_YEAR,
      lastUpdated: '12 May 2024 1:00',
      publicationFrequency: PublicationFrequency.EVERY_X_DAYS,
      publicationStart: '12 May 2024',
      lastReportPublished: '12 May 2024 2:00',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
