import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { BannerType } from '@registry-web/shared/banner/banner-type.enum';

@Component({
  selector: 'app-cancel-pending-activation-banner',
  template: `
    <app-banner [type]="BannerType.SUCCESS">
      <span>
        You have successfully cancelled the addition of the account '{{
          selectedTrustedAccountDescription
        }}' in the trusted account list.
      </span>
    </app-banner>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelPendingActivationBannerComponent {
  readonly BannerType = BannerType;
  @Input() selectedTrustedAccountDescription: string;
}
