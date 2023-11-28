import { SearchTransactionsResultsComponent } from './search-transactions-results.component';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { SortableColumnDirective } from '@shared/search/sort/sortable-column.directive';
import { RouterTestingModule } from '@angular/router/testing';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import {
  AccountAccessPipe,
  ApiEnumTypesPipe,
  GdsDateTimeShortPipe,
  ProtectPipe,
} from '@shared/pipes';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { mockClass, MockType } from '../../../../../testing/mocker';
import { provideMockStore } from '@ngrx/store/testing';
import { AccountAccessService } from '@registry-web/auth/account-access.service';
import { SortService } from '@shared/search/sort/sort.service';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { By } from '@angular/platform-browser';
import { Transaction, transactionStatusMap } from '@shared/model/transaction';

describe('SearchTransactionsResultsComponent', () => {
  let component: SearchTransactionsResultsComponent;
  let fixture: ComponentFixture<SearchTransactionsResultsComponent>;
  let authApiService: MockType<AuthApiService>;
  let accountAccessService: MockType<AccountAccessService>;

  const sortParameters: SortParameters = {
    sortField: 'lastUpdated',
    sortDirection: 'DESC',
  };

  beforeEach(
    waitForAsync(() => {
      authApiService = mockClass(AuthApiService);
      accountAccessService = mockClass(AccountAccessService);
      TestBed.configureTestingModule({
        declarations: [
          SearchTransactionsResultsComponent,
          SortableColumnDirective,
          GovukTagComponent,
          ProtectPipe,
          AccountAccessPipe,
          GdsDateTimeShortPipe,
          ApiEnumTypesPipe,
        ],
        imports: [RouterTestingModule],
        providers: [
          SortService,
          { provide: AuthApiService, useValue: authApiService },
          { provide: AccountAccessService, useValue: accountAccessService },
          provideMockStore(),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchTransactionsResultsComponent);
    component = fixture.componentInstance;
    component.sortParameters = sortParameters;
    component.transactionStatusMap = transactionStatusMap;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be sortable by default', () => {
    expect(component.isSortable).toEqual(true);
  });

  it('should display the link for transferringAccount when not external and user has access', () => {
    const mockResults: Transaction[] = [];

    const mockTransaction: Transaction = {
      id: 'testID',
      type: 'TransferAllowances',
      units: {
        quantity: 10,
        type: 'testUnitType',
      },
      transferringAccount: {
        title: 'Test Account',
        ukRegistryIdentifier: 'test',
        ukRegistryFullIdentifier: 'testFull',
        externalAccount: false,
        userHasAccess: true,
        accountStatus: 'OPEN',
      },
      acquiringAccount: {
        title: 'Test Account 2',
        ukRegistryIdentifier: 'test2',
        ukRegistryFullIdentifier: 'testFull2',
        externalAccount: true,
        userHasAccess: false,
        accountStatus: 'SUSPENDED',
      },
      lastUpdated: '2023-06-07',
      status: 'COMPLETED',
      runningBalance: {
        quantity: 20,
        type: 'testRunningBalanceType',
      },
      reversedByIdentifier: 'testReversedByIdentifier',
      reversesIdentifier: 'testReversesIdentifier',
    };

    mockResults.push(mockTransaction);
    component.results = mockResults;
    component.isAdmin = false;
    fixture.detectChanges();

    const linkElement = fixture.debugElement.query(By.css('td:first-child a'));

    expect(linkElement).toBeTruthy();
  });

  it('should display the link for transferringAccount when not external, user has access, and account status is not SUSPENDED or user is admin', () => {
    const mockTransaction: Transaction = {
      id: 'UK123456',
      type: 'TransferAllowances',
      units: {
        quantity: 10,
        type: 'allowances',
      },
      transferringAccount: {
        title: 'Test Account',
        ukRegistryIdentifier: 'test',
        ukRegistryFullIdentifier: 'testFull',
        externalAccount: false,
        userHasAccess: true,
        accountStatus: 'OPEN',
      },
      acquiringAccount: {
        title: 'Test Account 2',
        ukRegistryIdentifier: 'test2',
        ukRegistryFullIdentifier: 'testFull2',
        externalAccount: true,
        userHasAccess: false,
        accountStatus: 'SUSPENDED',
      },
      lastUpdated: '2023-06-07',
      status: 'COMPLETED',
      runningBalance: {
        quantity: 20,
        type: 'testRunningBalanceType',
      },
      reversedByIdentifier: 'testReversedByIdentifier',
      reversesIdentifier: 'testReversesIdentifier',
    };

    component.results = [mockTransaction];
    component.isAdmin = false; // Test with a non-admin user
    fixture.detectChanges();

    const linkElement = fixture.debugElement.query(
      By.css('.govuk-table__cell:nth-child(3) a')
    );

    expect(linkElement).toBeTruthy();
  });

  it('should not display the link for acquiringAccount when account is SUSPENDED even if user has access and it is not external', () => {
    const mockTransaction: Transaction = {
      id: 'UK123456',
      type: 'TransferAllowances',
      units: {
        quantity: 10,
        type: 'allowances',
      },
      transferringAccount: {
        title: 'Test Account',
        ukRegistryIdentifier: 'test',
        ukRegistryFullIdentifier: 'testFull',
        externalAccount: false,
        userHasAccess: true,
        accountStatus: 'OPEN',
      },
      acquiringAccount: {
        title: 'Test Account 2',
        ukRegistryIdentifier: 'test2',
        ukRegistryFullIdentifier: 'testFull2',
        externalAccount: false,
        userHasAccess: true,
        accountStatus: 'SUSPENDED',
      },
      lastUpdated: '2023-06-07',
      status: 'COMPLETED',
      runningBalance: {
        quantity: 20,
        type: 'testRunningBalanceType',
      },
      reversedByIdentifier: 'testReversedByIdentifier',
      reversesIdentifier: 'testReversesIdentifier',
    };

    component.results = [mockTransaction];
    component.isAdmin = false; // Test with a non-admin user
    fixture.detectChanges();

    const linkElement = fixture.debugElement.query(
      By.css('.govuk-table__cell:nth-child(4) a')
    );

    expect(linkElement).toBeNull(); // When account is SUSPENDED, the link should not exist
  });
});
