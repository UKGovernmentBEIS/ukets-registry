import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectBusinessCheckResult } from '../../reducers';
import { Observable } from 'rxjs';
import { canGoBack } from '@shared/shared.action';
import { BusinessCheckResult } from '@shared/model/transaction';

@Component({
  selector: 'app-proposal-submitted-container',
  template: `
    <app-proposal-submitted
      [businessCheckResult]="businessCheckResult$ | async"
    ></app-proposal-submitted>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProposalSubmittedContainerComponent implements OnInit {
  businessCheckResult$: Observable<BusinessCheckResult>;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.businessCheckResult$ = this.store.select(selectBusinessCheckResult);
  }
}
