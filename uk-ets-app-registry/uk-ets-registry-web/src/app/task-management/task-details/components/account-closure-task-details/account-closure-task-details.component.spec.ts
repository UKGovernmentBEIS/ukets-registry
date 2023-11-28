import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountClosureTaskDetailsComponent } from './account-closure-task-details.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RequestType } from '@task-management/model';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { AccountType } from '@shared/model/account';

describe('AccountClosureTaskDetailsComponent', () => {
  let component: AccountClosureTaskDetailsComponent;
  let fixture: ComponentFixture<AccountClosureTaskDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [AccountClosureTaskDetailsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountClosureTaskDetailsComponent);
    component = fixture.componentInstance;
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.ACCOUNT_CLOSURE_REQUEST,
      accountDetails: {
        name: 'accountName',
        accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
        accountNumber: 'UK-100-111111-11',
        accountHolderName: 'accountHolderName',
        accountHolderId: '111111',
        accountStatus: 'OPEN',
        complianceStatus: null,
        address: null,
        openingDate: '2022-01-10 18:37:50.234947',
        closingDate: null,
        closureReason: null,
        accountTypeEnum: AccountType.OPERATOR_HOLDING_ACCOUNT,
        billingEmail1: null,
        billingEmail2: null,
      },
      permitId: 'P1234',
      monitoringPlanId: null,
      closureComment: 'test',
      noActiveAR: false,
      allocationClassification: 'OVER_ALLOCATED',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('For OHAs the permitIdentifier is correctly rendered', () => {
    fixture.detectChanges();
    expect(
      component.getSummaryListItems().some((e) => e.key.label === 'Permit ID')
    ).toBeTruthy();
    expect(
      component
        .getSummaryListItems()
        .some((e) => e.key.label === 'Monitoring Plan ID')
    ).toBeFalsy();
  });

  test('For AOHAs the monitoringPlanIdentifier is correctly rendered', () => {
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.ACCOUNT_CLOSURE_REQUEST,
      accountDetails: {
        name: 'accountName',
        accountType: AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
        accountNumber: 'UK-100-111111-11',
        accountHolderName: 'accountHolderName',
        accountHolderId: '111111',
        accountStatus: 'OPEN',
        complianceStatus: null,
        address: null,
        openingDate: '2022-01-10 18:37:50.234947',
        closingDate: null,
        closureReason: null,
        accountTypeEnum: AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
        billingEmail1: null,
        billingEmail2: null,
      },
      permitId: null,
      monitoringPlanId: 'M12345',
      closureComment: 'test',
      noActiveAR: false,
      allocationClassification: 'OVER_ALLOCATED',
    };
    fixture.detectChanges();
    expect(
      component.getSummaryListItems().some((e) => e.key.label === 'Permit ID')
    ).toBeFalsy();
    expect(
      component
        .getSummaryListItems()
        .some((e) => e.key.label === 'Monitoring Plan ID')
    ).toBeTruthy();
  });

  test('For non OHAs and AOHAs the permitIdentifier and monitoringPlanIdentifier are not rendered', () => {
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.ACCOUNT_CLOSURE_REQUEST,
      accountDetails: {
        name: 'accountName',
        accountType: AccountType.TRADING_ACCOUNT,
        accountNumber: 'UK-100-111111-11',
        accountHolderName: 'accountHolderName',
        accountHolderId: '111111',
        accountStatus: 'OPEN',
        complianceStatus: null,
        address: null,
        openingDate: '2022-01-10 18:37:50.234947',
        closingDate: null,
        closureReason: null,
        accountTypeEnum: AccountType.TRADING_ACCOUNT,
        billingEmail1: null,
        billingEmail2: null,
      },
      permitId: null,
      monitoringPlanId: null,
      closureComment: 'test',
      noActiveAR: false,
      allocationClassification: null,
    };
    fixture.detectChanges();
    expect(
      component.getSummaryListItems().some((e) => e.key.label === 'Permit ID')
    ).toBeFalsy();
    expect(
      component
        .getSummaryListItems()
        .some((e) => e.key.label === 'Monitoring Plan ID')
    ).toBeFalsy();
  });
});
