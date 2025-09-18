import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { map } from 'rxjs';
import { selectOriginRoute } from '../../store/recovery-methods-change.selectors';
import { canGoBack } from '@registry-web/shared/shared.action';

@Component({
  selector: 'app-recovery-method-changed-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <div
          class="govuk-panel govuk-panel--confirmation govuk-!-margin-bottom-7"
        >
          <h1 class="govuk-panel__title">{{ message$ | async }}</h1>
        </div>
      </div>
    </div>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <p class="govuk-body">
          <a
            [routerLink]="['/user-details/my-profile']"
            class="govuk-link govuk-link--no-visited-state"
          >
            Back to the user details page
          </a>
        </p>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecoveryMethodChangedConfirmationComponent implements OnInit {
  message$ = this.activatedRoute.data.pipe(
    map((data) => data.message || 'The recovery method has been updated')
  );

  constructor(
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }
}
