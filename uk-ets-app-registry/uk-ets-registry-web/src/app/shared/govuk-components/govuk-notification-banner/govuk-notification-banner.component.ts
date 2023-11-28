import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { GovukNotificationBannerType } from '@shared/govuk-components';

@Component({
  selector: 'app-govuk-notification-banner',
  templateUrl: './govuk-notification-banner.component.html',
  styleUrls: ['./govuk-notification-banner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GovukNotificationBannerComponent {
  @Input() role: 'alert' | 'region' = 'region';
  @Input() type: GovukNotificationBannerType = 'info';
  @Input() title: string;
}
