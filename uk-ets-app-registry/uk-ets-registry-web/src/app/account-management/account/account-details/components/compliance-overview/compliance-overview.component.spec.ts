import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GovukTagComponent } from '@shared/govuk-components';
import { MockProtectPipe } from 'src/testing/mock-protect.pipe';

import { ComplianceOverviewComponent } from './compliance-overview.component';
import { ZeroAmountToDashPipe } from '@account-management/account/account-details/pipes/zero-amount-to-dash.pipe';
import { NullAmountToDashPipe } from '@account-management/account/account-details/pipes/null-amount-to-dash.pipe';

describe('ComplianceOverviewComponent', () => {
  let component: ComplianceOverviewComponent;
  let fixture: ComponentFixture<ComplianceOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ComplianceOverviewComponent,
        GovukTagComponent,
        MockProtectPipe,
        ZeroAmountToDashPipe,
        NullAmountToDashPipe,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ComplianceOverviewComponent);
    component = fixture.componentInstance;
    component.isAdmin = true;
    component.complianceOverviewResult = {
      totalVerifiedEmissions: undefined,
      totalNetSurrenders: 0,
      currentComplianceStatus: 'C',
    };
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should report 0 surrender balance with admin warning', () => {
    fixture.detectChanges();
    expect(component.getSurrenderBalance()).toEqual(0);
    expect(component.getWarning()).toBe(
      'You need to report emissions for all years of the reporting period. Upload emissions through ETS administration.'
    );
  });

  it('should report 0 surrender balance with representative warning', () => {
    component.isAdmin = false;
    fixture.detectChanges();
    expect(component.getSurrenderBalance()).toEqual(0);
    expect(component.getWarning()).toBe(
      'You need to report emissions for all years of the reporting period.'
    );
  });

  it('should report negative surrender balance and warning', () => {
    component.complianceOverviewResult = {
      totalVerifiedEmissions: 600,
      totalNetSurrenders: 0,
      currentComplianceStatus: 'B',
    };
    fixture.detectChanges();
    expect(component.getSurrenderBalance()).toBe(-600);
    expect(component.getWarning()).toBe('You need to surrender allowances.');
  });

  it('should report verified emissions with undefined surrender balance', () => {
    component.complianceOverviewResult = {
      totalVerifiedEmissions: 20000,
      totalNetSurrenders: undefined,
      currentComplianceStatus: 'B',
    };
    fixture.detectChanges();
    expect(component.getSurrenderBalance()).toEqual(-20000);
    expect(component.getWarning()).toBe('You need to surrender allowances.');
  });

  it('should report null with undefined surrenders and emissions', () => {
    component.complianceOverviewResult = {
      totalVerifiedEmissions: undefined,
      totalNetSurrenders: undefined,
      currentComplianceStatus: 'B',
    };
    fixture.detectChanges();
    expect(component.getSurrenderBalance()).toBeNull();
    expect(component.getWarning()).toBe('You need to surrender allowances.');
  });

  it('should report null with null surrenders and emissions', () => {
    component.complianceOverviewResult = {
      totalVerifiedEmissions: null,
      totalNetSurrenders: null,
      currentComplianceStatus: 'B',
    };
    fixture.detectChanges();
    expect(component.getSurrenderBalance()).toBeNull();
    expect(component.getWarning()).toBe('You need to surrender allowances.');
  });
});
