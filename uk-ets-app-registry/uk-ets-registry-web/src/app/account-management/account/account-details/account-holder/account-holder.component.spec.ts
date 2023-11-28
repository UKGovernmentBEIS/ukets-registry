import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountHolderComponent } from './account-holder.component';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from '../../../../auth/auth.reducer';
import { AuthApiService } from '../../../../auth/auth-api.service';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { SharedAccountHolderComponent } from '@shared/components/account/account-holder/shared-account-holder.component';
import { IdentificationDocumentationListComponent } from '@shared/components/identification-documentation-list';
import { AccountHolderContactComponent } from '@shared/components/account/account-holder-contact/account-holder-contact.component';
import {
  FormatUkDatePipe,
  GdsDateTimeShortPipe,
  IndividualPipe,
  IndividualFirstAndMiddleNamesPipe,
  OrganisationPipe,
  ProtectPipe,
  GovernmentPipe,
} from '@shared/pipes';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { AccountHolderType, AccountType } from '@shared/model/account';
import { RequestDocPipe } from '@shared/pipes/request-doc.pipe';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';

describe('AccountHolderComponent', () => {
  const TEST_ACCOUNT_ID = '123456';
  const MOCK_CURRENT_URL = 'the current url';
  let component: AccountHolderComponent;
  let fixture: ComponentFixture<AccountHolderComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          StoreModule.forRoot({
            auth: fromAuth.reducer,
          }),
          RouterModule.forRoot([]),
        ],
        declarations: [
          AccountHolderComponent,
          SharedAccountHolderComponent,
          IdentificationDocumentationListComponent,
          AccountHolderContactComponent,
          GdsDateTimeShortPipe,
          ThreeLineAddressComponent,
          IndividualPipe,
          OrganisationPipe,
          GovernmentPipe,
          PhoneNumberComponent,
          FormatUkDatePipe,
          RequestDocPipe,
          IndividualFirstAndMiddleNamesPipe,
          ProtectPipe,
        ],
        providers: [
          { provide: AuthApiService, useValue: MockAuthApiService },
          { provide: APP_BASE_HREF, useValue: '/' },
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                params: {
                  accountId: TEST_ACCOUNT_ID,
                },
                _routerState: {
                  url: MOCK_CURRENT_URL,
                },
              },
            },
          },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountHolderComponent);
    component = fixture.componentInstance;
    component.account = {
      identifier: 1,
      accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
      accountHolder: null,
      accountHolderContactInfo: null,
      accountDetails: {
        name: 'Component Test account',
        accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
        accountNumber: 'UK86876',
        accountHolderName: 'A Holder',
        accountHolderId: '12345',
        complianceStatus: 'NOT_APPLICABLE',
        accountStatus: 'OPEN',
        address: null,
      },
      kyotoAccountType: null,
      operator: null,
      authorisedRepresentatives: [],
      trustedAccountListRules: null,
      complianceStatus: '',
      balance: 0,
      unitType: '',
      governmentAccount: false,
      trustedAccountList: [],
      transactionsAllowed: false,
      canBeClosed: false,
      pendingARRequests: [],
    };
    component.accountHolder = {
      id: 1,
      type: AccountHolderType.INDIVIDUAL,
      details: {
        name: 'Name',
        firstName: 'first name',
        lastName: 'last name',
        birthDate: {
          day: '01',
          month: '02',
          year: '1980',
        },
        countryOfBirth: 'GR',
      },
      address: null,
    };
    component.accountHolderContactInfo = {
      primaryContact: null,
      alternativeContact: null,
    };
    component.countries = [
      {
        'index-entry-number': '',
        'entry-number': '',
        'entry-timestamp': '',
        key: 'GR',
        item: [
          {
            country: '',
            'end-date': '',
            'official-name': '',
            name: '',
            'citizen-names': '',
          },
        ],
        callingCode: '',
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
