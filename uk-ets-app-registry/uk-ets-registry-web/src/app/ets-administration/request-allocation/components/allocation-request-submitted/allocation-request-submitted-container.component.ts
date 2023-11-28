import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { BusinessCheckResult } from '@shared/model/transaction';
import { businessCheckResult } from '@request-allocation/reducers';

@Component({
  selector: 'app-allocation-request-submitted-container',
  template: `
    <app-allocation-request-submitted
      [businessCheckResult]="businessCheckResult$ | async"
    ></app-allocation-request-submitted>
  `
})
export class AllocationRequestSubmittedContainerComponent implements OnInit {
  businessCheckResult$: Observable<BusinessCheckResult>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.businessCheckResult$ = this.store.select(businessCheckResult);
  }
}
