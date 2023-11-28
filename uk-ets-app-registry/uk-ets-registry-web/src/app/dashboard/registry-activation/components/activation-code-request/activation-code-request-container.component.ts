import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { EnrolmentKey } from '@user-management/user-details/model';
import {
  selectEnrolmentKeyDetails,
  selectUserDetailsEmail
} from '@user-management/user-details/store/reducers';
import {
  requestActivationCode,
  submitNewRegistryActivationCodeRequest
} from '../../actions/registry-activation.actions';

@Component({
  selector: 'app-activation-code-request-container',
  template: `
    <app-activation-code-request
      [enrolmentKeyDetails]="enrolmentKeyDetails$ | async"
      [email]="email$ | async"
      (submitRequest)="onSubmitRequest()"
    ></app-activation-code-request>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ActivationCodeRequestContainerComponent implements OnInit {
  constructor(private store: Store) {}

  email$: Observable<string>;
  enrolmentKeyDetails$: Observable<EnrolmentKey>;

  ngOnInit(): void {
    this.store.dispatch(requestActivationCode({ backRoute: '/dashboard' }));

    this.email$ = this.store.select(selectUserDetailsEmail);
    this.enrolmentKeyDetails$ = this.store.select(selectEnrolmentKeyDetails);
  }

  onSubmitRequest() {
    this.store.dispatch(submitNewRegistryActivationCodeRequest());
  }
}
