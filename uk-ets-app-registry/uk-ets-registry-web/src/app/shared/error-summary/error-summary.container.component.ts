import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Observable } from 'rxjs';
import { IErrorSummary } from '@shared/error-summary';
import {
  selectErrorSummary,
  selectSetBackToErrorBasedPath,
} from '@shared/shared.selector';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-error-summary-container',
  template: `
    <app-error-summary
      [errorSummary]="errorSummary$ | async"
      [goBackToErrorBasedPath]="backToErrorBasedPath$ | async"
    ></app-error-summary>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorSummaryContainerComponent {
  errorSummary$: Observable<IErrorSummary>;
  backToErrorBasedPath$: Observable<string>;

  constructor(private store: Store) {
    this.errorSummary$ = this.store.select(selectErrorSummary);
    this.backToErrorBasedPath$ = this.store.select(
      selectSetBackToErrorBasedPath
    );
  }
}
