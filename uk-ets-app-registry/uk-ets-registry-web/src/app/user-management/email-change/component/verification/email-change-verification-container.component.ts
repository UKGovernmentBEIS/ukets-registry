import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectNewEmail } from '@email-change/reducer/email-change.selector';
import { navigateTo } from '@shared/shared.action';
import { EmailChangeRoutePath } from '@email-change/model';

@Component({
  selector: 'app-email-change-verification-container',
  template: `
    <app-email-change-verification
      [newEmail]="newEmail$ | async"
      (restartEmailChangeWizard)="onRestartWizard()"
    ></app-email-change-verification>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmailChangeVerificationContainerComponent implements OnInit {
  newEmail$: Observable<string>;
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.newEmail$ = this.store.select(selectNewEmail);
  }

  onRestartWizard() {
    this.store.dispatch(
      navigateTo({
        route: `/${EmailChangeRoutePath.BASE_PATH}`
      })
    );
  }
}
