import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AccountAllocationComponent } from './account-allocation.component';
import { AllocationWarningsComponent } from '../account-allocation/allocation-warnings/allocation-warnings.component';
import { AllocationTableComponent } from '../account-allocation/allocation-table/allocation-table.component';
import { By } from '@angular/platform-browser';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import {
  AccountAllocation,
  AllocationStatus,
} from '@shared/model/account/account-allocation';
import { selectAccountAllocation } from '@account-management/account/account-details/account.selector';
import { RouterTestingModule } from '@angular/router/testing';
import { clearErrors } from '@shared/shared.action';
import { GovukTagComponent } from '@shared/govuk-components';
import { GroupedNatAndNerAllocationTableComponent } from './grouped-allocation-table/grouped-nat-and-ner-allocation-table.component';
import { OperatorType } from '@shared/model/account';
import { AllocationType } from '@shared/model/allocation';

describe('AllocationStatusComponent', () => {
  let component: AccountAllocationComponent;
  let fixture: ComponentFixture<AccountAllocationComponent>;
  let mockStore: MockStore;

  function mockNatAndNerAccountAllocation(): AccountAllocation {
    return {
      standard: {
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
          allocated: 110,
          entitlement: 100,
          remaining: -10,
        },
      },
      underNewEntrantsReserve: {
        annuals: [
          {
            status: AllocationStatus.WITHHELD,
            remaining: -10,
            allocated: 110,
            entitlement: 100,
            year: 2020,
            eligibleForReturn: true,
            excluded: false,
          },
        ],
        totals: {
          allocated: 110,
          entitlement: 100,
          remaining: -10,
        },
      },
      groupedAllocations: [],
      allocationClassification: AllocationStatus.WITHHELD,
      totals: {
        allocated: 220,
        entitlement: 200,
        remaining: -20,
      },
    };
  }

  function mockYearlyAccountAllocation(): AccountAllocation {
    return {
      standard: {
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
          allocated: 110,
          entitlement: 100,
          remaining: -10,
        },
      },
      underNewEntrantsReserve: {
        annuals: [],
        totals: {
          allocated: null,
          entitlement: null,
          remaining: null,
        },
      },
      groupedAllocations: [],
      allocationClassification: null,
      totals: {
        allocated: null,
        entitlement: null,
        remaining: null,
      },
    };
  }

  function mockNerAccountAllocation(): AccountAllocation {
    return {
      standard: {
        annuals: [],
        totals: {
          allocated: null,
          entitlement: null,
          remaining: null,
        },
      },
      underNewEntrantsReserve: {
        annuals: [
          {
            status: AllocationStatus.WITHHELD,
            remaining: -10,
            allocated: 110,
            entitlement: 100,
            year: 2020,
            eligibleForReturn: true,
            excluded: false,
          },
        ],
        totals: {
          allocated: 110,
          entitlement: 100,
          remaining: -10,
        },
      },
      groupedAllocations: [],
      allocationClassification: null,
      totals: {
        allocated: null,
        entitlement: null,
        remaining: null,
      },
    };
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [
          AccountAllocationComponent,
          AllocationWarningsComponent,
          AllocationTableComponent,
          GroupedNatAndNerAllocationTableComponent,
          GovukTagComponent,
        ],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountAllocationComponent);
    component = fixture.componentInstance;
    component.accountId = '10000079';
    component.canRequestUpdate = true;
    mockStore = TestBed.inject(MockStore);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('Given the stored account allocation is null, then it should render nothing', () => {
    mockStore.overrideSelector(selectAccountAllocation, null);
    fixture.detectChanges();
    expect(fixture.debugElement.children.length).toBe(0);
  });

  it(`Given that there is NAT and NER allocation, then it should render the table`, () => {
    mockStore.overrideSelector(
      selectAccountAllocation,
      mockNatAndNerAccountAllocation()
    );
    component.operatorType = OperatorType.INSTALLATION;
    fixture.detectChanges();

    expect(component.getAllocationTableType()).toEqual(AllocationType.NAT);

    //Table Headers
    let headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[0];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Year');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[1];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Entitlement');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[2];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Allocated');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[3];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain(
      'Remaining to be allocated'
    );

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[4];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('To be returned');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[5];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Withhold status');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[6];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Exclusion status');
  });

  it(`Given that there is NAT allocation, then it should render the table`, () => {
    mockStore.overrideSelector(
      selectAccountAllocation,
      mockYearlyAccountAllocation()
    );
    component.operatorType = OperatorType.INSTALLATION;
    fixture.detectChanges();

    expect(component.getAllocationTableType()).toEqual(AllocationType.NAT);

    //Table Headers
    let headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[0];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Year');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[1];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Entitlement');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[2];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Allocated');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[3];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain(
      'Remaining to be allocated'
    );

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[4];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('To be returned');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[5];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Withhold status');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[6];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Exclusion status');
  });

  it(`Given that there is NER allocation, then it should render the table`, () => {
    mockStore.overrideSelector(
      selectAccountAllocation,
      mockNerAccountAllocation()
    );
    component.operatorType = OperatorType.INSTALLATION;
    fixture.detectChanges();

    expect(component.getAllocationTableType()).toEqual(AllocationType.NAT);

    //Table Headers
    let headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[0];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Year');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[1];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Entitlement');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[2];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Allocated');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[3];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain(
      'Remaining to be allocated'
    );

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[4];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('To be returned');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[5];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Withhold status');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[6];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Exclusion status');
  });

  it(`Given that there is NAVAT allocation, then it should render the table`, () => {
    mockStore.overrideSelector(
      selectAccountAllocation,
      mockYearlyAccountAllocation()
    );
    component.operatorType = OperatorType.AIRCRAFT_OPERATOR;
    fixture.detectChanges();

    expect(component.getAllocationTableType()).toEqual(AllocationType.NAVAT);

    //Table Headers
    let headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[0];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Year');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[1];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Entitlement');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[2];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Allocated');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[3];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain(
      'Remaining to be allocated'
    );

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[4];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('To be returned');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[5];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Withhold status');

    headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[6];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Exclusion status');
  });

  it('should clear the errors on destroy', () => {
    const spyStore = spyOn(mockStore, 'dispatch');
    fixture.destroy();
    expect(spyStore).toHaveBeenCalledWith(clearErrors());
  });
});
