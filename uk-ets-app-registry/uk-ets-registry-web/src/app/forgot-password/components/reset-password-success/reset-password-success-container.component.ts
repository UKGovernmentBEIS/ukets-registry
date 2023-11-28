import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectResetPasswordEmail } from '../../store/reducers';

@Component({
  selector: 'app-reset-password-success-container',
  template: `
    <app-reset-password-success [emailAddress]="emailAddress$ | async">
    </app-reset-password-success>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResetPasswordSuccessContainerComponent implements OnInit {
  emailAddress$: Observable<string>;
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.emailAddress$ = this.store.select(selectResetPasswordEmail);
  }
}
