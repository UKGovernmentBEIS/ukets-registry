import { Component, input, output, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { RegulatorNoticeTaskDetails } from '@shared/task-and-regulator-notice-management/model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { REGULATOR_NOTICE_LIST_PATH } from '@regulator-notice-management/list';

@Component({
  selector: 'app-regulator-notice-details-header',
  templateUrl: './regulator-notice-details-header.component.html',
  styleUrls: ['../../../../shared/sub-headers/styles/sub-header.scss'],
  standalone: true,
  imports: [SharedModule, RouterLink],
})
export class RegulatorNoticeDetailsHeaderComponent {
  readonly noticeDetails = input.required<RegulatorNoticeTaskDetails>();
  readonly completeTask = output<void>();

  readonly moreInfo = signal(false);
  readonly listPath = REGULATOR_NOTICE_LIST_PATH;

  toggleMoreInfo() {
    this.moreInfo.set(!this.moreInfo());
  }

  onCompleteTask() {
    this.completeTask.emit();
  }
}
