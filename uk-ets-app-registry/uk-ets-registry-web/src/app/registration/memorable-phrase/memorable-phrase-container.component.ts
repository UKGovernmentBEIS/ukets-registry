import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { IUser } from '@shared/user';
import { selectUser } from '@registry-web/registration/registration.selector';
import { updateUserMemorablePhrase } from '@registry-web/registration/registration.actions';

@Component({
  selector: 'app-memorable-phrase-container',
  template: `
    <app-memorable-phrase
      [user]="user$ | async"
      [caption]="'Create a registry sign in'"
      [header]="'Create a memorable phrase'"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-memorable-phrase>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MemorablePhraseContainerComponent implements OnInit {
  user$: Observable<IUser>;

  readonly nextRoute = '/registration/choose-password';
  readonly previousRoute = '/registration/work-details';

  constructor(private store: Store, private _router: Router) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: this.previousRoute }));
    this.user$ = this.store.select(selectUser);
  }

  onContinue(value) {
    this.store.dispatch(
      updateUserMemorablePhrase({
        user: value,
      })
    );
    this._router.navigate([this.nextRoute], {
      skipLocationChange: false,
    });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
