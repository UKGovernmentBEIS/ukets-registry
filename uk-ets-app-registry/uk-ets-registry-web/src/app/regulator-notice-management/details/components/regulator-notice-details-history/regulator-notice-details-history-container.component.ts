import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Store } from '@ngrx/store';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { navigateToUserProfile } from '@shared/shared.action';
import { SharedModule } from '@registry-web/shared/shared.module';
import { HistoryCommentsFormComponent } from '@registry-web/shared/components/history-comments-form';
import {
  RegulatorNoticeDetailsActions,
  selectHistory,
  selectNoticeDetails,
} from '@regulator-notice-management/details/store';

@Component({
  selector: 'app-regulator-notice-details-history-container',
  template: `
    <app-history-comments-form
      [task]="noticeDetails$ | async"
      (addComment)="onAddComment($event)"
    />
    <app-domain-events
      [domainEvents]="history$ | async"
      [isAdmin]="isAdmin$ | async"
      (navigate)="navigateToUserPage($event)"
    />
  `,
  standalone: true,
  imports: [SharedModule, HistoryCommentsFormComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorNoticeDetailsHistoryContainerComponent {
  private readonly store = inject(Store);
  private readonly router = inject(Router);

  readonly history$ = this.store.select(selectHistory);
  readonly noticeDetails$ = this.store.select(selectNoticeDetails);
  readonly isAdmin$ = this.store.select(isAdmin);

  constructor() {
    this.noticeDetails$.pipe(takeUntilDestroyed()).subscribe((noticeDetails) =>
      this.store.dispatch(
        RegulatorNoticeDetailsActions.FETCH_HISTORY({
          requestId: noticeDetails.requestId,
        })
      )
    );
  }

  onAddComment({ comment, requestId }: { comment: string; requestId: string }) {
    this.store.dispatch(
      RegulatorNoticeDetailsActions.ADD_HISTORY_COMMENT({ comment, requestId })
    );
  }

  navigateToUserPage(urid: string) {
    this.store.dispatch(
      navigateToUserProfile({
        goBackRoute: this.router.url,
        userProfileRoute: '/user-details/' + urid,
      })
    );
  }
}
