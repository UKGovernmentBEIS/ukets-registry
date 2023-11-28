import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { AllowanceReport } from '../../model';
import { selectIssuanceAllocationStatuses } from '../../store/reducers';
import { loadIssuanceAllocationStatus } from '../../store/actions';

@Component({
  selector: 'app-issuance-allocation-statuses-container',
  template: `
    <app-issuance-allocation-statuses
      [issuanceAllocationStatuses]="issuanceAllocationStatuses$ | async"
    >
    </app-issuance-allocation-statuses>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class IssuanceAllocationStatusesContainerComponent implements OnInit {
  issuanceAllocationStatuses$: Observable<AllowanceReport[]>;
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(loadIssuanceAllocationStatus());

    this.issuanceAllocationStatuses$ = this.store.select(
      selectIssuanceAllocationStatuses
    );
  }
}
