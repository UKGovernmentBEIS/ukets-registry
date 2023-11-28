import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { BannerType } from '@registry-web/shared/banner/banner-type.enum';

@Component({
  selector: 'app-pending-request-allocation-warning',
  template: `
    <app-banner
      [type]="BannerType.SUCCESS"
      *ngIf="isCancelAllocationSuccessDisplayed"
    >
      <span> You have successfully cancelled the pending allocations. </span>
    </app-banner>
    <div class="govuk-grid-row" *ngIf="isAllocationPendingDisplayed">
      <div class="govuk-grid-column-full">
        <div class="govuk-warning-text">
          <span class="govuk-warning-text__icon" aria-hidden="true">!</span>
          <strong class="govuk-warning-text__text govuk-!-margin-bottom-1">
            <span class="govuk-warning-text__assistive">Warning</span>
            There are pending allocations that have not been executed yet.
          </strong>

          <a
            [routerLink]="[]"
            (click)="onCancelAllocations()"
            class="govuk-link dont-print govuk-link--no-visited-state govuk-!-margin-left-7"
          >
            <span class="govuk-!-margin-left-1">Cancel allocations</span>
          </a>
        </div>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PendingRequestAllocationWarningComponent {
  readonly BannerType = BannerType;

  @Input() isCancelAllocationSuccessDisplayed: boolean;

  @Input() isAllocationPendingDisplayed: boolean;

  @Output() cancelAllocations = new EventEmitter<void>();

  onCancelAllocations() {
    this.cancelAllocations.emit();
  }
}
