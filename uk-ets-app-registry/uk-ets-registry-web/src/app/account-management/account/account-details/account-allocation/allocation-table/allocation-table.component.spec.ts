import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllocationTableComponent } from './allocation-table.component';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { AllocationStatus } from '@shared/model/account';
import { provideMockStore } from '@ngrx/store/testing';
import { ActivatedRoute } from '@angular/router';
import { GovukTagComponent } from '@shared/govuk-components';

const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));

describe('AllocationTableComponent', () => {
  let component: AllocationTableComponent;
  let fixture: ComponentFixture<AllocationTableComponent>;
  const ALLOCATION_STATUS_LABELS: Record<
    AllocationStatus,
    { label: string; description?: string }
  > = {
    ALLOWED: { label: 'Allowed' },
    WITHHELD: { label: 'Withheld' },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        provideMockStore(),
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
      ],
      declarations: [AllocationTableComponent, GovukTagComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AllocationTableComponent);
    component = fixture.componentInstance;
    component.ariaDescription = 'allocation status';
    component.allocation = {
      annuals: [
        {
          year: 2021,
          entitlement: 100,
          allocated: 100,
          remaining: 0,
          status: AllocationStatus.ALLOWED,
          eligibleForReturn: false,
          excluded: false,
        },
        {
          year: 2022,
          entitlement: 200,
          allocated: 0,
          remaining: 200,
          status: AllocationStatus.ALLOWED,
          eligibleForReturn: false,
          excluded: false,
        },
        {
          year: 2023,
          entitlement: 100,
          allocated: 200,
          remaining: -100,
          status: AllocationStatus.ALLOWED,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        remaining: 90,
        entitlement: 300,
        allocated: 210,
      },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the table headers correctly', () => {
    const expectedValues = [
      'Year',
      'Entitlement',
      'Allocated',
      'Remaining to be allocated',
      'To be returned',
      'Withhold status',
      'Exclusion status',
      '',
    ];
    const headers = fixture.debugElement.queryAll(By.css('th'));
    expect(headers.length).toBe(expectedValues.length);
    headers.forEach((header, index) => {
      expect(headers[index].nativeElement.textContent.trim()).toEqual(
        expectedValues[index]
      );
    });
  });

  it('should render the column values correctly', () => {
    const rows = fixture.debugElement.queryAll(
      By.css('tbody tr.govuk-table__row:not(.total-allocation)')
    );
    expect(rows.length).toBe(component.allocation.annuals.length);
    const firstAnnualRow = rows[0];

    const firstAnnual = component.allocation.annuals[0];
    const expectedFirstAnnualRowValues = [
      firstAnnual.year.toString(),
      firstAnnual.entitlement.toString(),
      firstAnnual.allocated.toString(),
      firstAnnual.remaining > 0 ? firstAnnual.remaining.toString() : '0',
      firstAnnual.remaining < 0 ? firstAnnual.remaining.toString() : '0',
      firstAnnual.status !== AllocationStatus.ALLOWED
        ? ALLOCATION_STATUS_LABELS[firstAnnual.status].label
        : '',
      '',
      '',
    ];
    const columns = firstAnnualRow.queryAll(By.css('td'));
    columns.forEach((column, index) => {
      expect(columns[index].nativeElement.textContent.trim()).toEqual(
        expectedFirstAnnualRowValues[index]
      );
    });

    const totalsRow = fixture.debugElement.query(By.css('tr.total-allocation'));
    const expectedTotalsRowColumnsValues = [
      'Total in phase',
      component.allocation.totals.entitlement.toString(),
      component.allocation.totals.allocated.toString(),
      '200',
      '100',
      '',
      '',
      '',
    ];
    const totalsColumns = totalsRow.queryAll(By.css('td'));
    totalsColumns.forEach((column, index) => {
      expect(totalsColumns[index].nativeElement.textContent.trim()).toEqual(
        expectedTotalsRowColumnsValues[index]
      );
    });
  });

  it('should render the remaining allocation column correctly', () => {
    function getRemainingAllocationElem(): DebugElement[] {
      return fixture.debugElement.queryAll(By.css('.remaining-col'));
    }

    component.allocation = {
      annuals: [
        {
          year: 2021,
          entitlement: 100,
          allocated: 10,
          remaining: 90,
          status: AllocationStatus.ALLOWED,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        remaining: 90,
        entitlement: 100,
        allocated: 10,
      },
    };
    fixture.detectChanges();
    const remainingToBeAllocatedColumn = getRemainingAllocationElem()[0];
    expect(
      remainingToBeAllocatedColumn.nativeElement.classList.contains(
        'remaining-allocation'
      )
    ).toBeTruthy();
    expect(
      remainingToBeAllocatedColumn.nativeElement.classList.contains(
        'under-allocated'
      )
    ).toBeTruthy();
    expect(
      remainingToBeAllocatedColumn.nativeElement.classList.contains(
        'over-allocated'
      )
    ).toBeFalsy();

    expect(
      remainingToBeAllocatedColumn.nativeElement.textContent.trim()
    ).toEqual(component.allocation.annuals[0].remaining.toString());

    const remainingToBeReturnedColumn = getRemainingAllocationElem()[1];
    expect(
      remainingToBeReturnedColumn.nativeElement.classList.contains(
        'remaining-allocation'
      )
    ).toBeFalsy();

    expect(
      remainingToBeReturnedColumn.nativeElement.classList.contains(
        'over-allocated'
      )
    ).toBeFalsy();

    expect(
      remainingToBeReturnedColumn.nativeElement.textContent.trim()
    ).toEqual('0');
  });
});
