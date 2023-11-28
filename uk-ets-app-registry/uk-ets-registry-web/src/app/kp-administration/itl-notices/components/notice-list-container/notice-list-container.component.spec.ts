import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeListContainerComponent } from './notice-list-container.component';
import { PaginatorComponent } from '@shared/search/paginator';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import { NoticeApiService } from '@kp-administration/itl-notices/service/notice-api.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoticeListComponent } from '@kp-administration/itl-notices/components';
import { NoticeApiServiceStub } from '../../../../../testing/notice-api.service.stub';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { selectNoticesState } from '@kp-administration/store';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { GovukTagComponent } from '@shared/govuk-components';

describe('NotificationListContainerComponent', () => {
  let component: NoticeListContainerComponent;
  let fixture: ComponentFixture<NoticeListContainerComponent>;
  let store: MockStore;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          CommonModule,
          FormsModule,
          RouterTestingModule,
          HttpClientTestingModule,
        ],
        declarations: [
          NoticeListContainerComponent,
          NoticeListComponent,
          PaginatorComponent,
          BackToTopComponent,
          GovukTagComponent,
          GdsDateTimeShortPipe,
        ],
        providers: [
          provideMockStore({
            selectors: [
              {
                selector: selectNoticesState,
                value: {
                  pagination: {
                    currentPage: 1,
                    totalResults: 20,
                    pageSize: 10,
                  },
                  pageParameters: { page: 0, pageSize: 10 },
                  sortParameters: {
                    sortDirection: 'ASC',
                    sortField: 'notificationIdentifier',
                  },
                  notices: [],
                },
              },
            ],
          }),
          {
            provide: NoticeApiService,
            useClass: NoticeApiServiceStub,
          },
          { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeListContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    store = TestBed.inject(MockStore);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch sort action on sort', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    component.onSort({ sortDirection: 'test', sortField: 'test' });

    expect(storeSpy).toHaveBeenCalledWith({
      criteria: null,
      pageParameters: { page: 0, pageSize: 10 },
      potentialErrors: null,
      sortParameters: { sortDirection: 'test', sortField: 'test' },
      type: '[ITL Notices] Change page',
      loadPageParametersFromState: true,
    });
  });

  it('should dispatch action on page event', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    component.onPageEvent({ pageSize: 10, page: 1 });

    expect(storeSpy).toHaveBeenCalledWith({
      criteria: null,
      pageParameters: { page: 1, pageSize: 10 },
      potentialErrors: null,
      sortParameters: {
        sortDirection: 'ASC',
        sortField: 'notificationIdentifier',
      },
      type: '[ITL Notices] Change page',
      loadPageParametersFromState: false,
    });
  });
});
