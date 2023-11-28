import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Observable } from 'rxjs';
import { DomainEvent } from '@shared/model/event';
import { selectAllocationTableEventHistory } from '../../store/reducers';
import { Store } from '@ngrx/store';
import { loadAllocationTableEventHistory } from '../../store/actions';
import { navigateToUserProfile } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { isAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-allocation-table-history-container',
  template: `
    <app-domain-events
      [domainEvents]="allocationTableEventHistory$ | async"
      [isAdmin]="isAdmin$ | async"
      (navigate)="navigateToUserPage($event)"
    ></app-domain-events>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AllocationTableHistoryContainerComponent implements OnInit {
  allocationTableEventHistory$: Observable<DomainEvent[]>;
  isAdmin$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.store.dispatch(loadAllocationTableEventHistory());
    this.allocationTableEventHistory$ = this.store.select(
      selectAllocationTableEventHistory
    );
    this.isAdmin$ = this.store.select(isAdmin);
  }

  navigateToUserPage(urid: string) {
    const goBackRoute = this.route.snapshot['_routerState'].url;
    const userProfileRoute = '/user-details/' + urid;
    this.store.dispatch(
      navigateToUserProfile({ goBackRoute, userProfileRoute })
    );
  }
}
