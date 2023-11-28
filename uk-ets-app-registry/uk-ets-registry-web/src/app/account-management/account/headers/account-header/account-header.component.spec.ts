import { AccountHeaderComponent } from '@account-management/account/headers/account-header/account-header.component';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { MockProtectPipe } from '../../../../../testing/mock-protect.pipe';
import { AccountAccessPipe } from '@shared/pipes';
import { mockClass, MockType } from '../../../../../testing/mocker';
import { AccountAccessService } from '@registry-web/auth/account-access.service';
import { Account, AccountHolderType } from '@shared/model/account';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { Store } from '@ngrx/store';
import { AccountStatusActions } from '@account-management/account/account-status/store/actions';
import { Component } from '@angular/core';
import { navigateToTransactionProposal } from '@shared/shared.action';

const mockAccount: Account = {
  identifier: 123456,
  accountType: null,
  accountHolder: {
    id: 12345,
    type: AccountHolderType.ORGANISATION,
    details: {
      name: 'Test Name',
      registrationNumber: '1234567',
      noRegistrationNumJustification: '',
    },
    address: {
      buildingAndStreet: '',
      buildingAndStreet2: '',
      buildingAndStreet3: '',
      postCode: '',
      townOrCity: '',
      stateOrProvince: '',
      country: '',
    },
  },
  kyotoAccountType: null,
  operator: null,
  authorisedRepresentatives: [],
  accountHolderContactInfo: null,
  trustedAccountListRules: { rule2: true, rule1: true, rule3: true },
  complianceStatus: null,
  balance: null,
  unitType: null,
  accountDetails: {
    accountHolderName: 'Account Holder 01',
    accountHolderId: '12345',
    complianceStatus: 'NOT_APPLICABLE',
    accountNumber: 'UK-1234-0000-1',
    name: 'Account Name 01',
    accountType: null,
    accountStatus: 'OPEN',
    address: null,
  } as any,
  trustedAccountList: null,
  governmentAccount: false,
  transactionsAllowed: true,
  canBeClosed: false,
  pendingARRequests: null,
  addedARs: 0,
  removedARs: 0,
};

@Component({
  selector: 'app-dummy-component',
  template: ``,
})
class DummyComponent {}

describe('AccountHeaderComponent', () => {
  let component: AccountHeaderComponent;
  let fixture: ComponentFixture<AccountHeaderComponent>;
  let accountAccessService: MockType<AccountAccessService>;
  let router: Router;
  let store: Store;

  beforeEach(
    waitForAsync(() => {
      accountAccessService = mockClass(AccountAccessService);
      TestBed.configureTestingModule({
        declarations: [
          AccountHeaderComponent,
          DummyComponent,
          GovukTagComponent,
          AccountAccessPipe,
          MockProtectPipe,
        ],
        imports: [
          RouterTestingModule.withRoutes([
            { path: 'transactions', component: DummyComponent },
          ]),
        ],
        providers: [
          { provide: AccountAccessService, useValue: accountAccessService },
          provideMockStore(),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountHeaderComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    store = TestBed.inject(Store);
    component.accountHeaderActionsVisibility = true;
    component.showBackToList = true;
    component.account = mockAccount;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch action to navigate to transaction proposal on button click', () => {
    const navigateSpy = jest.spyOn(store, 'dispatch');
    const button = fixture.debugElement.queryAll(By.css('.govuk-button'));

    button[0].nativeElement.click();

    expect(navigateSpy).toHaveBeenCalledWith(
      navigateToTransactionProposal({ routeSnapshotUrl: '' })
    );
  });

  it('should dispatch action on change status click', () => {
    const dispatchSpy = jest.spyOn(store, 'dispatch');
    const anchors = fixture.debugElement.queryAll(By.css('a'));

    const changeStatus = anchors.filter(
      (a) => a.nativeElement.textContent === 'Change status'
    )[0];
    expect(changeStatus).toBeTruthy();

    changeStatus.nativeElement.click();
    expect(dispatchSpy).toHaveBeenCalledWith(
      AccountStatusActions.fetchLoadAndShowAllowedAccountStatusActions()
    );
  });

  it('should hide back to list', () => {
    component.showBackToList = false;
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.back-to-list-btn'))).toBeFalsy();
  });

  it('should show back to list', () => {
    component.showBackToList = true;
    fixture.detectChanges();
    expect(
      fixture.debugElement.query(By.css('.back-to-list-btn'))
    ).toBeTruthy();
  });
});
