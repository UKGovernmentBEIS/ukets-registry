import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { IssuanceAllocationOverviewComponent } from './issuance-allocation-overview.component';
import {
  AllocationTableHistoryContainerComponent,
  IssuanceAllocationStatusesComponent,
} from '..';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IssuanceAllocationStatusesContainerComponent } from '@registry-web/ets-administration/issuance-allocation-status/components';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import { DomainEventsComponent } from '@shared/components/event/domain-events/domain-events.component';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { By } from '@angular/platform-browser';
import { AuthApiService } from 'src/app/auth/auth-api.service';
import { MockAuthApiService } from 'src/testing/mock-auth-api-service';
import {
  selectAllocationTableEventHistory,
  selectIssuanceAllocationStatuses,
} from '@registry-web/ets-administration/issuance-allocation-status/store/reducers';
import { MockProtectPipe } from '../../../../../testing/mock-protect.pipe';
import { isAdmin } from '@registry-web/auth/auth.selector';

describe('IssuanceAllocationOverviewComponent', () => {
  let component: IssuanceAllocationOverviewComponent;
  let fixture: ComponentFixture<IssuanceAllocationOverviewComponent>;
  let router: Router;
  let store: MockStore<any>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [
          IssuanceAllocationOverviewComponent,
          AllocationTableHistoryContainerComponent,
          IssuanceAllocationStatusesContainerComponent,
          IssuanceAllocationStatusesComponent,
          BackToTopComponent,
          DomainEventsComponent,
          GdsDateTimeShortPipe,
          MockProtectPipe,
        ],
        providers: [
          provideMockStore({
            selectors: [
              { selector: selectIssuanceAllocationStatuses, value: [] },
              { selector: selectAllocationTableEventHistory, value: [] },
              { selector: isAdmin, value: [] },
            ],
          }),
          { provide: AuthApiService, useValue: MockAuthApiService },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    router = TestBed.inject(Router);
    store = TestBed.inject(Store) as MockStore<any>;
    fixture = TestBed.createComponent(IssuanceAllocationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render title(s) in h1-tag(s)', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain(
      'View issuance and allocation status'
    );
    const subtitles = fixture.debugElement.queryAll(By.css('.govuk-heading-m'));
    expect(subtitles[0].nativeElement.textContent).toContain(
      'Issuance and allocation status'
    );
    expect(subtitles[1].nativeElement.textContent).toContain(
      'History and comments'
    );
  });

  it('should navigate on button click', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    const button = fixture.debugElement.query(By.css('button'));

    button.nativeElement.click();
    expect(navigateSpy).toHaveBeenCalledWith(
      ['ets-administration/allocation-table'],
      { skipLocationChange: false }
    );
  });
});
