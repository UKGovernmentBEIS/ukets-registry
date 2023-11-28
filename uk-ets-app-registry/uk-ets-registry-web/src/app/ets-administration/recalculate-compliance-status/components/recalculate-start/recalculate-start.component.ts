import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RecalculateComplianceRequestStatus } from '@recalculate-compliance-status/model';
import { BannerType } from '@shared/banner/banner-type.enum';

@Component({
  selector: 'app-recalculate-start',
  templateUrl: './recalculate-start.component.html',
  styles: [],
})
export class RecalculateStartComponent {
  @Input()
  recalculateComplianceRequestStatus: RecalculateComplianceRequestStatus;

  @Output() readonly recalculateClick = new EventEmitter<void>();

  readonly BannerType = BannerType;

  onContinue(): void {
    this.recalculateClick.next();
  }
}
