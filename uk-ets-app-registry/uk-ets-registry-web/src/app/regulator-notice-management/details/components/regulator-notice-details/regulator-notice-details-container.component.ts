import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  RegulatorNoticeDetailsActions,
  selectNoticeDetails,
} from '@regulator-notice-management/details/store';
import { RegulatorNoticeDetailsComponent } from '@regulator-notice-management/details/components/regulator-notice-details/regulator-notice-details.component';
import { canGoBackToList } from '@registry-web/shared/shared.action';
import { REGULATOR_NOTICE_DETAILS_PATH } from '@regulator-notice-management/details/regulator-notice-details.const';
import { RequestType } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-regulator-notice-details-container',
  standalone: true,
  imports: [SharedModule, RegulatorNoticeDetailsComponent],
  template: `
    <app-regulator-notice-details
      [noticeDetails]="noticeDetails()"
      (goToAccountDetails)="goToAccountDetails($event)"
      (downloadFile)="onDownloadFile()"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorNoticeDetailsContainerComponent {
  private readonly store = inject(Store);
  private readonly router = inject(Router);

  readonly noticeDetails = toSignal(this.store.select(selectNoticeDetails));

  goToAccountDetails(accountNumber: string) {
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/${REGULATOR_NOTICE_DETAILS_PATH}/${this.noticeDetails()?.requestId}`,
        extras: { skipLocationChange: false },
      })
    );

    this.router.navigate(['/account', accountNumber]);
  }

  onDownloadFile() {
    this.store.dispatch(
      RegulatorNoticeDetailsActions.FETCH_FILE({
        info: {
          fileId: this.noticeDetails()?.file?.id,
          taskRequestId: this.noticeDetails()?.requestId,
          taskType: 'REGULATOR_NOTICE' as RequestType,
        },
      })
    );
  }
}
