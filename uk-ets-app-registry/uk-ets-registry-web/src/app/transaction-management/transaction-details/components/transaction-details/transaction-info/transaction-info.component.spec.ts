import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionInfoComponent } from './transaction-info.component';
import { RouterTestingModule } from '@angular/router/testing';
import {
  AccountAccessPipe,
  ApiEnumTypesPipe,
  TransactionAtrributesPipe,
} from '@shared/pipes';
import { MockAuthApiService } from '../../../../../../testing/mock-auth-api-service';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { MockAccountAccessService } from '../../../../../../testing/mock-account-access-service';
import { MockProtectPipe } from '../../../../../../testing/mock-protect.pipe';
import { AccountAccessService } from '@registry-web/auth/account-access.service';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import {
  ItlNotificationSummaryComponent,
  TransactionConnectionSummaryComponent,
} from '@shared/components/transactions';
import { SummaryListComponent } from '@shared/summary-list';
import { By } from '@angular/platform-browser';

describe('TransactionInfoComponent', () => {
  let component: TransactionInfoComponent;
  let fixture: ComponentFixture<TransactionInfoComponent>;

  let store: MockStore<any>;
  let mockAuthStateSelector: any;
  const initial = {
    authModel: {
      authenticated: true,
      showLoading: false,
      id: '',
      urid: '',
      username: '',
      roles: [],
      firstName: '',
      lastName: '',
    },
    checkingKeycloakStatus: false,
    keycloakError: '',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        TransactionInfoComponent,
        ItlNotificationSummaryComponent,
        AccountAccessPipe,
        MockProtectPipe,
        ApiEnumTypesPipe,
        TransactionAtrributesPipe,
        TransactionConnectionSummaryComponent,
        SummaryListComponent,
      ],
      imports: [RouterTestingModule],
      providers: [
        provideMockStore(),
        { provide: AuthApiService, useValue: MockAuthApiService },
        { provide: AccountAccessService, useClass: MockAccountAccessService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionInfoComponent);
    component = fixture.componentInstance;

    fixture.componentInstance.transactionDetails = {
      hasAccessToAcquiringAccount: true,
      hasAccessToTransferringAccount: true,
      identifier: '',
      taskIdentifier: '',
      type: 'IssueOfAAUsAndRMUs',
      status: '',
      quantity: '50',
      acquiringAccountName: '',
      externalAcquiringAccount: false,
      acquiringAccountIdentifier: 0,
      acquiringAccountType: '',
      acquiringAccountRegistryCode: '',
      acquiringAccountFullIdentifier: 'GB-100-1002-1-84',
      transferringAccountName: 'asds',
      externalTransferringAccount: false,
      transferringAccountIdentifier: 1002,
      transferringAccountStatus: 'OPEN',
      acquiringAccountStatus: 'SUSPENDED',
      transferringAccountType: '',
      transferringRegistryCode: '',
      transferringAccountFullIdentifier: 'GB-100-1002-1-84',
      started: '',
      lastUpdated: new Date(),
      blocks: [],
      responses: [],
      unitType: 'AAU',
      attributes: null,
      itlNotification: null,
      executionDateTime: new Date(),
    };

    store = TestBed.inject(Store) as MockStore<any>;

    mockAuthStateSelector = store.overrideSelector('authModel', initial);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call go to account with specific identifier', () => {
    const anchor = fixture.debugElement.nativeElement.querySelector('a');

    expect(anchor.getAttribute('href')).toEqual('/account/1002');
  });

  it('should display the link for open transferring account and should be equal to transferringAccountIdentifier 1002', () => {
    const linkElement = fixture.debugElement.query(
      By.css('[data-testid="transferring-account-link"]')
    );
    expect(linkElement).toBeTruthy();

    expect(
      fixture.componentInstance.transactionDetails.transferringAccountIdentifier
    ).toEqual(1002);
  });

  it('should not display the link for acquiringAccount when account is SUSPENDED even if user has access and it is not external', () => {
    component.isAdmin = false; // Test with a non-admin user
    fixture.detectChanges();

    const linkElement = fixture.debugElement.query(
      By.css('[data-testid="acquiring-account-link"]')
    );
    expect(linkElement).toBeNull(); // When account is SUSPENDED, the link should not exist
  });
});
