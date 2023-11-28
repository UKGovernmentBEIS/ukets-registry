import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AllocationRequestTaskDetailsComponent } from './allocation-request-task-details.component';
import { AllocationCategoryPipe } from '@registry-web/shared/pipes';
import {
  AllocationCategory,
  AllocationType,
  AllocationOverviewRow,
} from '@registry-web/shared/model/allocation';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  AllocationRequestTaskDetails,
  RequestType,
  TaskOutcome,
} from '@registry-web/task-management/model';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('AllocationRequestTaskDetailsComponent', () => {
  let component: AllocationRequestTaskDetailsComponent;
  let fixture: ComponentFixture<AllocationRequestTaskDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [AllocationRequestTaskDetailsComponent],
      imports: [SharedModule],
      providers: [AllocationCategoryPipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AllocationRequestTaskDetailsComponent);
    component = fixture.componentInstance;
  });

  beforeEach(() => {
    const taskDetails: AllocationRequestTaskDetails = {
      taskType: RequestType.ALLOCATION_REQUEST,
      allocationOverview: {
        category: AllocationCategory.AircraftOperator,
        year: 0,
        totalQuantity: 0,
        total: {
          allocationType: AllocationType.NAVAT,
          accounts: 0,
          quantity: 0,
          excludedAccounts: 0,
          withheldAccounts: 0,
          closedAndFullySuspendedAccounts: 0,
          transferPendingAccounts: 0,
        },
        rows: {
          [AllocationType.NAVAT as AllocationType]: {
            allocationType: AllocationType.NAVAT,
            accounts: 0,
            quantity: 0,
            excludedAccounts: 0,
            withheldAccounts: 0,
            closedAndFullySuspendedAccounts: 0,
            transferPendingAccounts: 0,
          },
          clear: function (): void {
            throw new Error('Function not implemented.');
          },
          delete: function (key: AllocationType): boolean {
            throw new Error('Function not implemented.');
          },
          forEach: function (
            callbackfn: (
              value: AllocationOverviewRow,
              key: AllocationType,
              map: Map<AllocationType, AllocationOverviewRow>
            ) => void,
            thisArg?: any
          ): void {
            throw new Error('Function not implemented.');
          },
          get: function (key: AllocationType): AllocationOverviewRow {
            throw new Error('Function not implemented.');
          },
          has: function (key: AllocationType): boolean {
            throw new Error('Function not implemented.');
          },
          set: function (
            key: AllocationType,
            value: AllocationOverviewRow
          ): Map<AllocationType, AllocationOverviewRow> {
            throw new Error('Function not implemented.');
          },
          size: 0,
          entries: function (): IterableIterator<
            [AllocationType, AllocationOverviewRow]
          > {
            throw new Error('Function not implemented.');
          },
          keys: function (): IterableIterator<AllocationType> {
            throw new Error('Function not implemented.');
          },
          values: function (): IterableIterator<AllocationOverviewRow> {
            throw new Error('Function not implemented.');
          },
          [Symbol.iterator]: function (): IterableIterator<
            [AllocationType, AllocationOverviewRow]
          > {
            throw new Error('Function not implemented.');
          },
          [Symbol.toStringTag]: '',
        },
      },
      natAccountName: '',
      nerAccountName: '',
      currentHoldings: 0,
      requestId: '',
      initiatorName: '',
      initiatorUrid: '',
      claimantName: '',
      claimantURID: '',
      taskStatus: '',
      requestStatus: TaskOutcome.APPROVED,
      initiatedDate: '',
      claimedDate: '',
      currentUserClaimant: false,
      completedByName: '',
      accountNumber: '',
      accountFullIdentifier: '',
      accountName: '',
      referredUserFirstName: '',
      referredUserLastName: '',
      referredUserURID: '',
      history: [],
      subTasks: [],
      parentTask: undefined,
    };

    component.taskDetails = taskDetails;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the "operators" when category is Aviation', () => {
    component.taskDetails.allocationOverview.category =
      AllocationCategory.AircraftOperator;
    component.ngOnInit();
    fixture.detectChanges();
    const excluded: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[5];
    expect(excluded.textContent.trim()).toContain('operators');
    const withheld: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[6];
    expect(withheld.textContent.trim()).toContain('operators');
    const pending: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[7];
    expect(pending.textContent.trim()).toContain('operators');
    const suspended: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[8];
    expect(suspended.textContent.trim()).toContain('operators');
  });

  it('should display the "installations" when category is Installation', () => {
    component.taskDetails.allocationOverview.category =
      AllocationCategory.Installation;
    component.ngOnInit();
    fixture.detectChanges();
    const excluded: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[5];
    expect(excluded.textContent.trim()).toContain('installations');
    const withheld: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[6];
    expect(withheld.textContent.trim()).toContain('installations');
    const pending: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[7];
    expect(pending.textContent.trim()).toContain('installations');
    const suspended: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[8];
    expect(suspended.textContent.trim()).toContain('installations');
  });

  it('should display the "operators/installations" when category is not defined (old data)', () => {
    component.taskDetails.allocationOverview.category = null;
    component.ngOnInit();
    fixture.detectChanges();
    const excluded: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[4];
    expect(excluded.textContent.trim()).toContain('operators/installations');
    const withheld: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[5];
    expect(withheld.textContent.trim()).toContain('operators/installations');
    const pending: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[6];
    expect(pending.textContent.trim()).toContain('operators/installations');
    const suspended: HTMLElement = fixture.nativeElement.querySelectorAll(
      '.govuk-summary-list__key'
    )[7];
    expect(suspended.textContent.trim()).toContain('operators/installations');
  });
});
