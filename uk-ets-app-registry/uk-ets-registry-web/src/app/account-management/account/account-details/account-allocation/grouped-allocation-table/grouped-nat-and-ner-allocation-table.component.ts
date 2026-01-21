import { Component, Input, OnInit } from '@angular/core';
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
})
export class GroupedNatAndNerAllocationTableComponent implements OnInit {
  @Input() ariaDescription: string;
  @Input() canRequestTransaction: boolean;
  @Input() groupedAllocationOverview: GroupedAllocationOverview;

  detailsOpen: any[];

  ngOnInit() {
    this.detailsOpen = Array(
      this.groupedAllocationOverview.groupedAllocations.length
    ).fill(false);
  }

  toggleDetails(index: number) {
    this.detailsOpen[index] = !this.detailsOpen[index];
  }

  allocationStatusLabels = ALLOCATION_STATUS_LABELS;
  allocationStatusAllowed = AllocationStatus.ALLOWED;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute
  ) {}

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
        return aa.standardAnnualAllocation.remaining > 0
          ? r + aa.standardAnnualAllocation.remaining
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
        return aa.standardAnnualAllocation.remaining < 0
          ? r + Math.abs(aa.standardAnnualAllocation.remaining)
          : r;
      }, 0) +
      annuals.reduce(function (r, aa) {
        return aa.nerAnnualAllocation.remaining < 0
          ? r + Math.abs(aa.nerAnnualAllocation.remaining)
          : r;
      }, 0)
    );
  }

  isNaNOrZeroReturnEmpty(num) {
    return isNaN(num) || num === 0 ? '' : num;
  }
}
