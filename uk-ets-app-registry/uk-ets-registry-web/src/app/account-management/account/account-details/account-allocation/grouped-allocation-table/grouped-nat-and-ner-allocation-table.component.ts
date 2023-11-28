import { Component, Input } from '@angular/core';
import {
  AggregatedAllocation,
  ALLOCATION_STATUS_LABELS,
  AllocationStatus,
  AnnualAllocation,
  GroupedAllocation,
  GroupedAllocationOverview,
} from '@shared/model/account/account-allocation';
import { AccountActions } from '@account-management/account/account-details';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { getUrlIdentifier } from '@shared/shared.util';
import { ReturnExcessAllocationType } from '@shared/model/allocation';

@Component({
  selector: 'app-grouped-allocation-table',
  templateUrl: './grouped-nat-and-ner-allocation-table.component.html',
  styleUrls: ['./grouped-nat-and-ner-allocation-table.component.scss'],
})
export class GroupedNatAndNerAllocationTableComponent {
  @Input() ariaDescription: string;
  @Input() allocationType: string;
  @Input() canRequestTransaction: boolean;
  @Input() standardAllocation: AggregatedAllocation;
  @Input() underNewEntrantsReserveAllocation: AggregatedAllocation;
  detailsOpen: any[];
  allocation: GroupedAllocationOverview;

  ngOnInit() {
    this.allocation = this.mergeAllocations();
    this.detailsOpen = Array(this.allocation.groupedAllocations.length).fill(
      false
    );
  }

  toggleDetails(index: number) {
    this.detailsOpen[index] = !this.detailsOpen[index];
  }

  allocationStatusLabels = ALLOCATION_STATUS_LABELS;
  allocationStatusAllowed = AllocationStatus.ALLOWED;

  constructor(private store: Store, private activatedRoute: ActivatedRoute) {}

  goToReturnExcessTransaction(
    natAnnual: AnnualAllocation,
    nerAnnual: AnnualAllocation
  ) {
    let allocationType: ReturnExcessAllocationType;

    if (
      Math.abs(natAnnual.remaining) > 0 &&
      Math.abs(nerAnnual.remaining) > 0
    ) {
      allocationType = 'NAT_AND_NER';
    } else if (
      Math.abs(natAnnual.remaining) > 0 &&
      Math.abs(nerAnnual.remaining) === 0
    )
      allocationType = 'NAT';
    else allocationType = 'NER';

    this.store.dispatch(
      AccountActions.prepareTransactionStateForReturnOfExcess({
        routeSnapshotUrl: getUrlIdentifier(
          this.activatedRoute.snapshot['_routerState'].url
        ),
        allocationYear: natAnnual.year,
        allocationType: allocationType,
        excessAmountPerAllocationAccount: {
          returnToAllocationAccountAmount:
            Math.abs(natAnnual.remaining) > 0
              ? Math.abs(natAnnual.remaining)
              : 0,
          returnToNewEntrantsReserveAccount:
            Math.abs(nerAnnual.remaining) > 0
              ? Math.abs(nerAnnual.remaining)
              : 0,
        },
      })
    );
  }

  getRemainingToBeAllocated(annuals: GroupedAllocation[]) {
    return (
      annuals.reduce(function (r, aa) {
        return aa.natAnnualAllocation.remaining > 0
          ? r + aa.natAnnualAllocation.remaining
          : r;
      }, 0) +
      annuals.reduce(function (r, aa) {
        return aa.nerAnnualAllocation.remaining > 0
          ? r + aa.nerAnnualAllocation.remaining
          : r;
      }, 0)
    );
  }

  getRemainingToBeReturned(annuals: GroupedAllocation[]) {
    return (
      annuals.reduce(function (r, aa) {
        return aa.natAnnualAllocation.remaining < 0
          ? r + Math.abs(aa.natAnnualAllocation.remaining)
          : r;
      }, 0) +
      annuals.reduce(function (r, aa) {
        return aa.nerAnnualAllocation.remaining < 0
          ? r + Math.abs(aa.nerAnnualAllocation.remaining)
          : r;
      }, 0)
    );
  }

  mergeAllocations(): GroupedAllocationOverview {
    const mergedGroupedAllocationOverview: GroupedAllocationOverview = {
      groupedAllocations: [],
      totals: {
        entitlement:
          (this.standardAllocation.totals.entitlement || 0) +
          (this.underNewEntrantsReserveAllocation.totals.entitlement || 0),
        allocated:
          (this.standardAllocation.totals.allocated || 0) +
          (this.underNewEntrantsReserveAllocation.totals.allocated || 0),
        remaining:
          (this.standardAllocation.totals.remaining || 0) +
          (this.underNewEntrantsReserveAllocation.totals.remaining || 0),
      },

      //TODO check this
      allocationClassification: AllocationStatus.WITHHELD,
    };

    const annuals = [
      ...this.standardAllocation.annuals,
      ...this.underNewEntrantsReserveAllocation.annuals,
    ];
    const annualAllocationMap: { [key: number]: AnnualAllocation } = {};

    // Group the annual allocations by year
    annuals.forEach((annualAllocation) => {
      const year = annualAllocation.year;

      if (!annualAllocationMap[year]) {
        annualAllocationMap[year] = {
          year,
          entitlement: 0,
          allocated: 0,
          remaining: 0,
          status: AllocationStatus.ALLOWED,
          eligibleForReturn: false,
          excluded: false,
        };
      }

      // Update the annual allocation based on the data from both json objects
      const existingAllocation = annualAllocationMap[year];
      const newEntitlement =
        existingAllocation.entitlement + annualAllocation.entitlement;
      const newAllocated =
        existingAllocation.allocated + annualAllocation.allocated;
      const newRemaining =
        existingAllocation.remaining + annualAllocation.remaining;
      const newStatus = this.decideAllocationStatus(annualAllocation);
      const newExcluded = this.decideExcluded(annualAllocation);

      annualAllocationMap[year] = {
        year,
        entitlement: newEntitlement,
        allocated: newAllocated,
        remaining: newRemaining,
        status: newStatus,
        eligibleForReturn:
          existingAllocation.eligibleForReturn ||
          annualAllocation.eligibleForReturn,
        excluded: newExcluded,
      };
    });

    // Construct the grouped allocation DTOs
    for (const year in annualAllocationMap) {
      const annualAllocation = annualAllocationMap[year];
      const natAnnualAllocation = this.standardAllocation.annuals.find(
        (a) => a.year === annualAllocation.year
      ) ?? {
        year: annualAllocation.year,
        entitlement: 0,
        allocated: 0,
        remaining: 0,
        status: AllocationStatus.ALLOWED,
        eligibleForReturn: false,
      };
      const nerAnnualAllocation =
        this.underNewEntrantsReserveAllocation.annuals.find(
          (a) => a.year === annualAllocation.year
        ) ?? {
          year: annualAllocation.year,
          entitlement: 0,
          allocated: 0,
          remaining: 0,
          status: AllocationStatus.ALLOWED,
          eligibleForReturn: false,
        };

      mergedGroupedAllocationOverview.groupedAllocations.push(<
        GroupedAllocation
      >{
        summedAnnualAllocationNatAndNer: annualAllocation,
        natAnnualAllocation: natAnnualAllocation,
        nerAnnualAllocation: nerAnnualAllocation,
      });
    }
    return mergedGroupedAllocationOverview;
  }

  decideAllocationStatus(allocation: AnnualAllocation): AllocationStatus {
    return allocation.status === AllocationStatus.WITHHELD
      ? AllocationStatus.WITHHELD
      : AllocationStatus.ALLOWED;
  }

  decideExcluded(allocation: AnnualAllocation) {
    if (allocation.excluded === null) {
      return false;
    }
    return allocation.excluded === true;
  }

  isNaNOrZeroReturnEmpty(num) {
    return isNaN(num) || num === 0 ? '' : num;
  }
}
