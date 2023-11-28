import { HoldingsComponent } from './holdings.component';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AccountHolderType, AccountType } from '@shared/model/account';
import { CommitmentPeriod, UnitType } from '@shared/model/transaction';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { UnitTypeSopRenderPipe } from '@shared/pipes';
import { HoldingsSummaryComponent } from '@account-management/account/account-details/holdings-summary/holdings-summary.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { MockProtectPipe } from 'src/testing/mock-protect.pipe';

describe('HoldingsComponent', () => {
  let component: HoldingsComponent;
  let fixture: ComponentFixture<HoldingsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [
          HoldingsComponent,
          MockProtectPipe,
          GovukTagComponent,
          HoldingsSummaryComponent,
          UnitTypeSopRenderPipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(HoldingsComponent);
    component = fixture.componentInstance;
    component.account = {
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
        accountStatus: null,
        address: null,
      },
      trustedAccountList: null,
      governmentAccount: false,
      transactionsAllowed: true,
      canBeClosed: false,
      pendingARRequests: null,
    };
    component.account.accountType = AccountType.PERSON_HOLDING_ACCOUNT;

    component.accountHoldingsResult = {
      totalAvailableQuantity: 20015,
      totalReservedQuantity: 133,
      currentComplianceStatus: 'A',
      shouldMeetEmissionsTarget: true,
      items: [
        {
          type: UnitType.AAU,
          originalPeriod: CommitmentPeriod.CP1,
          applicablePeriod: CommitmentPeriod.CP2,
          availableQuantity: 278,
          reservedQuantity: 35,
          subjectToSop: false,
        },
        {
          type: UnitType.RMU,
          originalPeriod: null,
          applicablePeriod: null,
          availableQuantity: 10,
          reservedQuantity: null,
          subjectToSop: false,
        },
      ],
      reservedUnitType: UnitType.RMU,
      availableUnitType: UnitType.RMU,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render a caption', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('caption').textContent).toContain(
      'Click on the unit to view more details.'
    );
  });

  test('should render Summary list in light grey color', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.ukets-background-light-grey')
    )[0];
    expect(key).toBeTruthy();
  });

  test('the Total available quantity is rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[0];
    expect(key.nativeElement.textContent.trim()).toEqual(
      'Total available quantity:'
    );
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[0];
    expect(value.nativeElement.textContent.trim()).toEqual(
      '20,015 Removal Units'
    );
  });

  test('the Total reserved quantity is rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[1];
    expect(key.nativeElement.textContent.trim()).toEqual(
      'Total reserved quantity:'
    );
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[1];
    expect(value.nativeElement.textContent.trim()).toEqual('133 Removal Units');
  });

  test('the current surrender status is not rendered', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[2];
    expect(key).toBeUndefined();
  });
});
