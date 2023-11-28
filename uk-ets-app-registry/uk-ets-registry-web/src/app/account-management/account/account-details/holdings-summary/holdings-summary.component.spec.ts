import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CommitmentPeriod, UnitType } from '@shared/model/transaction';
import { By } from '@angular/platform-browser';
import { HoldingsSummaryComponent } from '@account-management/account/account-details/holdings-summary/holdings-summary.component';
import { MockProtectPipe } from 'src/testing/mock-protect.pipe';
import { GovukTagComponent } from '@shared/govuk-components';

describe('HoldingsSummaryComponent', () => {
  let component: HoldingsSummaryComponent;
  let fixture: ComponentFixture<HoldingsSummaryComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          HoldingsSummaryComponent,
          GovukTagComponent,
          MockProtectPipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(HoldingsSummaryComponent);
    component = fixture.componentInstance;
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

  test('the current surrender status is rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[2];
    expect(key.nativeElement.textContent.trim()).toEqual(
      'Current surrender status:'
    );
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[2];
    expect(value.nativeElement.textContent.trim()).toEqual('A');
  });

  test('no warning message is rendered', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-warning-text__text')
    )[0];
    expect(key).toBeUndefined();
  });

  test('the surrender allowances warning message is rendered correctly', () => {
    component.accountHoldingsResult = {
      totalAvailableQuantity: 20015,
      totalReservedQuantity: 133,
      currentComplianceStatus: 'B',
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

    const key = fixture.debugElement.queryAll(
      By.css('.govuk-warning-text__text')
    )[0];
    expect(key.nativeElement.textContent.trim()).toEqual(
      'Warning You need to surrender allowances. See more details in Emissions and Surrenders section.'
    );
  });

  test('the emissions reporting warning message is rendered correctly', () => {
    component.accountHoldingsResult = {
      totalAvailableQuantity: 20015,
      totalReservedQuantity: 133,
      currentComplianceStatus: 'C',
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

    const key = fixture.debugElement.queryAll(
      By.css('.govuk-warning-text__text')
    )[0];
    expect(key.nativeElement.textContent.trim()).toEqual(
      'Warning You need to report emissions for all years of the reporting period. See more details in Emissions and Surrenders section.'
    );
  });
});
