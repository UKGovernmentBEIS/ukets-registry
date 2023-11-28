import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { selectResetPasswordEmail } from '../../store/reducers';

@Component({
  selector: 'app-email-sent-container',
  template: `
    <app-email-sent [emailAddress]="emailAddress$ | async"> </app-email-sent>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmailSentContainerComponent {
  emailAddress$: Observable<string>;

  constructor(private store: Store) {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.emailAddress$ = this.store.select(selectResetPasswordEmail);
  }
}
