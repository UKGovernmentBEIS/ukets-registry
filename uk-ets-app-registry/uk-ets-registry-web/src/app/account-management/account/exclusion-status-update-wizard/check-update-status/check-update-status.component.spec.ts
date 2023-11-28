import { APP_BASE_HREF } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import {
  AccountType,
  OperatorType,
  Regulator,
} from '@registry-web/shared/model/account';
import {
  AircraftOperatorPipe,
  InstallationPipe,
} from '@registry-web/shared/pipes';

import { CheckUpdateStatusComponent } from './check-update-status.component';

describe('CheckUpdateStatusComponent', () => {
  let component: CheckUpdateStatusComponent;
  let fixture: ComponentFixture<CheckUpdateStatusComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([])],
        declarations: [CheckUpdateStatusComponent, InstallationPipe],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          InstallationPipe,
          AircraftOperatorPipe,
        ],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
      }).compileComponents();
    })
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CheckUpdateStatusComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateStatusComponent);
    component = fixture.componentInstance;
    component.year = 2021;
    component.exclusionStatus = true;
    component.account = {
      identifier: 1234,
      accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
      operator: {
        type: OperatorType.INSTALLATION,
        regulator: Regulator.EA,
        firstYear: '2021',
      },
      accountHolder: null,
      accountHolderContactInfo: null,
      accountDetails: null,
      authorisedRepresentatives: null,
      trustedAccountListRules: null,
      complianceStatus: null,
      balance: null,
      unitType: null,
      governmentAccount: null,
      trustedAccountList: null,
      transactionsAllowed: null,
      pendingARRequests: null,
      canBeClosed: true,
      kyotoAccountType: null,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render the title and values correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[0];
    expect(key.nativeElement.textContent.trim()).toEqual(
      'Year to update the operator exclusion status'
    );
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[0];
    expect(value.nativeElement.textContent.trim()).toEqual('2021');
    const key2 = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[1];
    expect(key2.nativeElement.textContent.trim()).toEqual(
      'Exclusion of the operator'
    );
    const value2 = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[1];
    expect(value2.nativeElement.textContent.trim()).toEqual('Yes');
    const title = fixture.debugElement.queryAll(By.css('.govuk-heading-m'))[0];
    expect(title.nativeElement.textContent.trim()).toEqual(
      'Installation details'
    );
  });
});
