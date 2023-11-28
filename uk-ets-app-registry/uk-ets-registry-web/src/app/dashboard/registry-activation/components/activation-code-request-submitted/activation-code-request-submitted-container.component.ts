import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { canGoBack } from '@shared/shared.action';
import { selectRequestId } from '../../reducers';

@Component({
  selector: 'app-request-submitted-container',
  template: `
    <app-activation-code-request-submitted
      [submittedIdentifier]="requestId$ | async"
    ></app-activation-code-request-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ActivationCodeRequestSubmittedContainerComponent
  implements OnInit {
  requestId$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.requestId$ = this.store.select(selectRequestId);
  }
}
