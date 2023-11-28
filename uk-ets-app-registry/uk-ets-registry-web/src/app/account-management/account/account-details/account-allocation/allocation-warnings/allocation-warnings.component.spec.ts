import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllocationWarningsComponent } from './allocation-warnings.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AllocationStatus } from '@shared/model/account/account-allocation';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

describe('AllocationWarningsComponent', () => {
  let component: AllocationWarningsComponent;
  let fixture: ComponentFixture<AllocationWarningsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [AllocationWarningsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AllocationWarningsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.WITHHELD,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.WITHHELD,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should set its flags', () => {
    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.WITHHELD,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.WITHHELD,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    // eslint-disable-next-line @angular-eslint/no-lifecycle-call
    component.ngOnInit();
    expect(component.withheld).toBeTruthy();
    expect(component.overAllocated).toBeFalsy();
    expect(component.pendingExcessAllocation).toBeTruthy();

    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    // eslint-disable-next-line @angular-eslint/no-lifecycle-call
    component.ngOnInit();
    expect(component.withheld).toBeFalsy();
    expect(component.overAllocated).toBeFalsy();
    expect(component.pendingExcessAllocation).toBeTruthy();

    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: 10,
          allocated: 90,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };

    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: 10,
          allocated: 90,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    // eslint-disable-next-line @angular-eslint/no-lifecycle-call
    component.ngOnInit();
    expect(component.withheld).toBeFalsy();
    expect(component.overAllocated).toBeFalsy();
    expect(component.pendingExcessAllocation).toBeFalsy();
  });

  it('should display the withheld warning only when the withheld flag is true', () => {
    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.WITHHELD,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.WITHHELD,
          remaining: -10,
          allocated: 110,
          entitlement: 100,
          year: 2020,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    fixture.detectChanges();

    function testWarningsDisplay(withheld: boolean, overallocated: boolean) {
      component.withheld = withheld;
      component.overAllocated = overallocated;
      fixture.detectChanges();
      const withheldWarning = fixture.debugElement.query(
        By.css('.withheld-warning')
      );
      const overallocatedWarning = fixture.debugElement.query(
        By.css('.overallocated-warning')
      );

      function checkWarning(flag: boolean, warning: DebugElement) {
        if (flag) {
          expect(warning).toBeTruthy();
        } else {
          expect(warning).toBeFalsy();
        }
      }
      checkWarning(withheld, withheldWarning);
      checkWarning(overallocated, overallocatedWarning);
    }
    testWarningsDisplay(false, false);
    testWarningsDisplay(false, true);
    testWarningsDisplay(true, false);
    testWarningsDisplay(true, true);
  });

  it('should display the overallocated and the pending excess warnings when an account is overallocated and eligibleForReturn is true for one year and false for another', () => {
    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: -10,
          allocated: 20,
          entitlement: 10,
          year: 2021,
          eligibleForReturn: false,
          excluded: false,
        },
        {
          status: AllocationStatus.ALLOWED,
          remaining: -1,
          allocated: 6,
          entitlement: 5,
          year: 2022,
          eligibleForReturn: true,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: -10,
          allocated: 20,
          entitlement: 10,
          year: 2021,
          eligibleForReturn: false,
          excluded: false,
        },
        {
          status: AllocationStatus.ALLOWED,
          remaining: -1,
          allocated: 6,
          entitlement: 5,
          year: 2022,
          eligibleForReturn: true,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    fixture.detectChanges();

    component.ngOnInit();
    expect(component.overAllocated).toBeTruthy();
    expect(component.pendingExcessAllocation).toBeTruthy();

    fixture.detectChanges();
    const pendingExcessAllocationWarning = fixture.debugElement.query(
      By.css('.pending-excess-warning')
    );
    const overallocatedWarning = fixture.debugElement.query(
      By.css('.overallocated-warning')
    );

    expect(pendingExcessAllocationWarning).toBeTruthy();
    expect(overallocatedWarning).toBeTruthy();
  });

  it('should hide the overallocated warning when the account is overallocated and eligibleForReturn is false for all years', () => {
    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: -10,
          allocated: 20,
          entitlement: 10,
          year: 2021,
          eligibleForReturn: false,
          excluded: false,
        },
        {
          status: AllocationStatus.ALLOWED,
          remaining: -1,
          allocated: 6,
          entitlement: 5,
          year: 2022,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: -10,
          allocated: 20,
          entitlement: 10,
          year: 2021,
          eligibleForReturn: false,
          excluded: false,
        },
        {
          status: AllocationStatus.ALLOWED,
          remaining: -1,
          allocated: 6,
          entitlement: 5,
          year: 2022,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    fixture.detectChanges();

    component.ngOnInit();
    expect(component.overAllocated).toBeFalsy();
    expect(component.pendingExcessAllocation).toBeTruthy();

    fixture.detectChanges();
    const pendingExcessAllocationWarning = fixture.debugElement.query(
      By.css('.pending-excess-warning')
    );
    const overallocatedWarning = fixture.debugElement.query(
      By.css('.overallocated-warning')
    );

    expect(overallocatedWarning).toBeFalsy();
    expect(pendingExcessAllocationWarning).toBeTruthy();
  });

  it('should display that allocations exists after last year warning when there are years with remaining allocations after LYVE', () => {
    component.lastYear = 2021;
    component.natAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: 10,
          allocated: 90,
          entitlement: 100,
          year: 2021,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    component.nerAllocation = {
      annuals: [
        {
          status: AllocationStatus.ALLOWED,
          remaining: 10,
          allocated: 90,
          entitlement: 100,
          year: 2022,
          eligibleForReturn: false,
          excluded: false,
        },
      ],
      totals: {
        allocated: 100,
        entitlement: 100,
        remaining: 100,
      },
    };
    fixture.detectChanges();

    component.ngOnInit();

    fixture.detectChanges();
    let allocationsAfterLastYearWarning = fixture.debugElement.query(
      By.css('.allocation-after-last-year')
    );

    expect(component.allocationsAfterLastYear).toBeTruthy();
    expect(allocationsAfterLastYearWarning).toBeTruthy();

    component.lastYear = 2022;
    fixture.detectChanges();
    component.ngOnInit();
    fixture.detectChanges();
    allocationsAfterLastYearWarning = fixture.debugElement.query(
      By.css('.allocation-after-last-year')
    );

    expect(component.allocationsAfterLastYear).toBeFalsy();
    expect(allocationsAfterLastYearWarning).toBeFalsy();
  });
});
