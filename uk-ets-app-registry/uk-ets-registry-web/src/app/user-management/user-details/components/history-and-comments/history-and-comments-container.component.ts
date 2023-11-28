import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { selectUserHistoryAndComments } from '@user-management/user-details/store/reducers';
import { Store } from '@ngrx/store';
import { DomainEvent } from '@shared/model/event';

@Component({
  selector: 'app-user-history-and-comments-container',
  template: `
    <app-domain-events [domainEvents]="userHistory$ | async"></app-domain-events
    >
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryAndCommentsContainerComponent implements OnInit {
  userHistory$: Observable<DomainEvent[]>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.userHistory$ = this.store.select(selectUserHistoryAndComments);
  }
}
