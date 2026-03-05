import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  isSeniorAdmin,
  isSeniorOrJuniorAdmin,
} from '@registry-web/auth/auth.selector';
import { SharedModule } from '@registry-web/shared/shared.module';
import { selectRequestId } from '@regulator-notice-management/details/store';
import {
  selectTaskNotes,
  TaskNotesComponent,
} from '@registry-web/notes/task-notes';

@Component({
  selector: 'app-regulator-notice-details-notes-container',
  template: `
    <app-task-notes
      [requestId]="requestId$ | async"
      [isAdmin]="isSeniorOrJuniorAdmin$ | async"
      [isSeniorAdmin]="isSeniorAdmin$ | async"
      [notes]="notes$ | async"
    />
  `,
  standalone: true,
  imports: [SharedModule, TaskNotesComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorNoticeDetailsNotesContainerComponent {
  private readonly store = inject(Store);

  readonly requestId$ = this.store.select(selectRequestId);
  readonly isSeniorAdmin$ = this.store.select(isSeniorAdmin);
  readonly isSeniorOrJuniorAdmin$ = this.store.select(isSeniorOrJuniorAdmin);
  readonly notes$ = this.store.select(selectTaskNotes);
}
