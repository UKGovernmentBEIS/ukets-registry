import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ArInAccount } from '@user-management/user-details/model';
import { Observable } from 'rxjs';
import { selectARsInAccountDetails } from '@user-management/user-details/store/reducers';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-ar-in-accounts-container',
  template: `
    <app-ar-in-accounts
      [arInAccount]="arInAccount$ | async"
    ></app-ar-in-accounts>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ArInAccountsContainerComponent implements OnInit {
  arInAccount$: Observable<ArInAccount[]>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.arInAccount$ = this.store.select(selectARsInAccountDetails);
  }
}
