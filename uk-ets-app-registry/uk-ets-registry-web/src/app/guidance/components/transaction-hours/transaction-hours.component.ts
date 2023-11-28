import { Component, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-guidance-transaction-hours',
  templateUrl: './transaction-hours.component.html',
})
export class TransactionHoursComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance`,
      })
    );
  }
}
