import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { IssuanceAllocationStatusesComponent } from './issuance-allocation-statuses.component';
import { AllowanceReport } from '../../model';
import { By } from '@angular/platform-browser';

describe('IssuanceAllocationStatusesComponent', () => {
  let component: IssuanceAllocationStatusesComponent;
  let fixture: ComponentFixture<IssuanceAllocationStatusesComponent>;

  const issuanceAllocationStatuses: AllowanceReport[] = [
    {
      description: '2021',
      cap: 500000,
      entitlement: 1000000,
      issued: 900000,
      allocated: 700000,
      forAuction: 50000,
      auctioned: 100000,
    },
    {
      description: '2022',
      cap: 500000,
      entitlement: 500000,
      issued: 100000,
      allocated: 0,
      forAuction: 0,
      auctioned: 0,
    },
    {
      description: 'Total in phase',
      cap: 2600000,
      entitlement: 1500000,
      issued: 900000,
      allocated: 700000,
      forAuction: 50000,
      auctioned: 100000,
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [IssuanceAllocationStatusesComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuanceAllocationStatusesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render table header with govuk-table__header', () => {
    component.issuanceAllocationStatuses = issuanceAllocationStatuses;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    const headerValues = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    );
    expect(headerValues[0].nativeElement.textContent).toContain('Year');
    expect(headerValues[1].nativeElement.textContent).toContain('CAP');
    expect(headerValues[2].nativeElement.textContent).toContain('Entitlement');
    expect(headerValues[3].nativeElement.textContent).toContain('Issued');
    expect(headerValues[4].nativeElement.textContent).toContain('Allocated');
  });

  test('should render table data correctly', () => {
    component.issuanceAllocationStatuses = issuanceAllocationStatuses;
    fixture.detectChanges();
    const row = fixture.debugElement.queryAll(By.css('.govuk-table__row'))[1];
    expect(row.children[0].nativeElement.textContent.trim()).toEqual('2021');
    expect(row.children[1].nativeElement.textContent.trim()).toEqual('500,000');
    expect(row.children[2].nativeElement.textContent.trim()).toEqual('1,000,000');
    expect(row.children[3].nativeElement.textContent.trim()).toEqual('900,000');
    expect(row.children[4].nativeElement.textContent.trim()).toEqual('700,000');
  });
});
