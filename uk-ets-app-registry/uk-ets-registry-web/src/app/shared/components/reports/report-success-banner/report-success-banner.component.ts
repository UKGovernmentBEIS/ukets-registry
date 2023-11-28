import { Component, Input } from '@angular/core';
import { BannerType } from '@shared/banner/banner-type.enum';

@Component({
  selector: 'app-report-success-banner',
  templateUrl: './report-success-banner.component.html',
})
export class ReportSuccessBannerComponent {
  @Input() isVisible: boolean;
  readonly BannerType = BannerType;
}
