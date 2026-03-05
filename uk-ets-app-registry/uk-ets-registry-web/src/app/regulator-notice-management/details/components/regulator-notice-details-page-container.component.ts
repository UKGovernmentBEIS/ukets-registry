import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  inject,
  OnInit,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { MenuItem } from '@registry-web/shared/model/navigation-menu';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  RegulatorNoticeDetailsActions,
  selectNoticeDetails,
} from '@regulator-notice-management/details/store';
import { RegulatorNoticeDetailsHeaderComponent } from '@regulator-notice-management/details/components/regulator-notice-details-header';
import {
  NOTES_LIST_PATH,
  selectTaskNotes,
  TaskNotesActions,
} from '@registry-web/notes/task-notes';
import { clearGoBackToListRoute } from '@registry-web/shared/shared.action';

@Component({
  selector: 'app-regulator-notice-details-page-container',
  standalone: true,
  imports: [SharedModule, RegulatorNoticeDetailsHeaderComponent, RouterModule],
  template: `
    <app-feature-header-wrapper>
      <app-regulator-notice-details-header
        [noticeDetails]="noticeDetails()"
        (completeTask)="onCompleteTask()"
      />
    </app-feature-header-wrapper>
    <app-sub-menu
      [subMenuItems]="subMenuItems()"
      [subMenuActive]="subMenuActive()"
    />
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorNoticeDetailsPageContainerComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly router = inject(Router);
  private readonly isAdmin = toSignal(this.store.select(isAdmin));

  private readonly routerEvents = toSignal(this.router.events);
  readonly subMenuActive = computed<string>(() => {
    this.routerEvents();
    return this.router.url;
  });

  readonly noticeDetails = toSignal(this.store.select(selectNoticeDetails));
  readonly notes = toSignal(this.store.select(selectTaskNotes));

  readonly subMenuItems = computed<MenuItem[]>(() => {
    const requestId = this.noticeDetails()?.requestId;
    const detailsMenuItem: MenuItem = {
      label: 'Notice details',
      routerLink: `/regulator-notice-details/${requestId}`,
      protectedScopes: [],
    };
    const historyMenuItem: MenuItem = {
      label: 'History and comments',
      routerLink: `/regulator-notice-details/${requestId}/history`,
      protectedScopes: [],
    };

    if (this.isAdmin()) {
      const notesCount = this.notes().length;
      return [
        detailsMenuItem,
        {
          label: `Notes${notesCount > 0 ? ` (${notesCount})` : ''}`,
          routerLink: `/regulator-notice-details/${requestId}/${NOTES_LIST_PATH}`,
          protectedScopes: [],
        },
        historyMenuItem,
      ];
    }

    return [detailsMenuItem, historyMenuItem];
  });

  constructor() {
    effect(
      () => {
        if (this.noticeDetails()?.requestId) {
          this.store.dispatch(
            TaskNotesActions.FETCH_TASK_NOTES({
              requestId: this.noticeDetails().requestId,
            })
          );
        }
      },
      { allowSignalWrites: true }
    );
  }

  ngOnInit(): void {
    this.store.dispatch(clearGoBackToListRoute());
  }

  onCompleteTask() {
    this.store.dispatch(RegulatorNoticeDetailsActions.COMPLETE());
  }
}
