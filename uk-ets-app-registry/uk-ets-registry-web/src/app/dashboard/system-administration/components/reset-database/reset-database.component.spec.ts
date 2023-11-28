import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetDatabaseComponent } from '..';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import {
  SystemAdministrationState,
  selectResetDatabaseResult,
} from '../../store/reducers';
import { Store, MemoizedSelector } from '@ngrx/store';
import { ResetDatabaseResult } from '../../model';
import { By } from '@angular/platform-browser';

describe('ResetDatabaseComponent', () => {
  let component: ResetDatabaseComponent;
  let fixture: ComponentFixture<ResetDatabaseComponent>;
  let mockStore: MockStore<SystemAdministrationState>;
  let mockResetDatabaseResultSelector: MemoizedSelector<
    SystemAdministrationState,
    ResetDatabaseResult
  >;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ResetDatabaseComponent],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetDatabaseComponent);
    component = fixture.componentInstance;
    mockStore = TestBed.inject(Store) as MockStore<any>;
    mockResetDatabaseResultSelector = mockStore.overrideSelector(
      selectResetDatabaseResult,
      {
        accountsResult: {
          governmentAccountsCreated: 7,
          sampleAccountsCreated: 9,
        },
        usersResult: {
          usersDeleted: 80,
          usersCreated: 14,
        },
      }
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('the users actions results are rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[0];
    expect(key.nativeElement.textContent.trim()).toEqual('Users');
    const p1 = fixture.debugElement
      .queryAll(By.css('.govuk-summary-list__value'))[0]
      .queryAll(By.css('.govuk-body'))[0];
    expect(p1.nativeElement.textContent.trim()).toEqual('80 users deleted');
    const p2 = fixture.debugElement
      .queryAll(By.css('.govuk-summary-list__value'))[0]
      .queryAll(By.css('.govuk-body'))[1];
    expect(p2.nativeElement.textContent.trim()).toEqual('14 users created');
  });

  test('the accounts actions results are rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[1];
    expect(key.nativeElement.textContent.trim()).toEqual('Accounts');
    const p2 = fixture.debugElement
      .queryAll(By.css('.govuk-summary-list__value'))[1]
      .queryAll(By.css('.govuk-body'))[0];
    expect(p2.nativeElement.textContent.trim()).toEqual(
      '9 Sample Accounts created'
    );
  });
});
