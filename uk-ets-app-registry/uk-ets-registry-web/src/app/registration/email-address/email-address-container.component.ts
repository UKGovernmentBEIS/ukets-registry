import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-email-address-container',
  template: `
    <app-email-address
      (emailAddress)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-email-address>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailAddressContainerComponent implements OnInit {
  readonly previousRoute = 'registration/emailInfo';

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: this.previousRoute }));
  }

  onContinue(email: string): void {
    this._router.navigate(['/registration/emailVerify', email], {
      skipLocationChange: true,
    });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
