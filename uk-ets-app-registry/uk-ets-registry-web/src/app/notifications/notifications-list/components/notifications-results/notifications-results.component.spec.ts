import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { APP_BASE_HREF } from '@angular/common';
import { NotificationsResultsComponent } from './notifications-results.component';
import { SortableTableDirective } from '@shared/search/sort/sortable-table.directive';
import { SortableColumnDirective } from '@shared/search/sort/sortable-column.directive';
import { PaginatorComponent } from '@shared/search/paginator';
import { GdsDatePipe, GdsDateTimePipe } from '@shared/pipes';
import { SortService } from '@shared/search/sort/sort.service';
import { GovukTagComponent } from '@shared/govuk-components';

describe('NotificationsResultsComponent', () => {
  let component: NotificationsResultsComponent;
  let fixture: ComponentFixture<NotificationsResultsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          NotificationsResultsComponent,
          UkRadioInputComponent,
          SortableTableDirective,
          SortableColumnDirective,
          PaginatorComponent,
          GdsDateTimePipe,
          GdsDatePipe,
          GovukTagComponent,
        ],
        providers: [SortService, { provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationsResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
