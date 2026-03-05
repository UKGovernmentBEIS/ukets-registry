import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { RegulatorNoticeTaskDetails } from '@shared/task-and-regulator-notice-management/model';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  selector: 'app-regulator-notice-details',
  standalone: true,
  imports: [RouterLink, SharedModule],
  templateUrl: './regulator-notice-details.component.html',
})
export class RegulatorNoticeDetailsComponent {
  readonly noticeDetails = input.required<RegulatorNoticeTaskDetails>();
  readonly goToAccountDetails = output<string>();
  readonly downloadFile = output<void>();

  onGoToAccountDetails() {
    this.goToAccountDetails.emit(this.noticeDetails()?.accountNumber);
  }

  onDownloadFile() {
    this.downloadFile.emit();
  }
}
