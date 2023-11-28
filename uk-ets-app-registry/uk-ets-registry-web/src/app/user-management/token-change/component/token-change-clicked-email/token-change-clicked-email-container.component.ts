import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { actionValidateEmailToken } from '@user-management/token-change/action/token-change.actions';

@Component({
  selector: 'app-token-change-clicked-email-container',
  template: `
    <app-token-change-clicked-email></app-token-change-clicked-email>
  `
})
export class TokenChangeClickedEmailContainerComponent implements OnInit {
  token: string;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.paramMap.get('token');
    this.store.dispatch(actionValidateEmailToken({ token: this.token }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
