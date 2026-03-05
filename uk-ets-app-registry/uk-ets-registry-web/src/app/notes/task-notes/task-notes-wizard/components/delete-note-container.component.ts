import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { TaskNotesActions } from '../../store/task-notes.actions';
import { DeleteNoteComponent } from '@notes/components/delete-note/delete-note.component';
import { TASK_NOTES_PARENT_DETAILS_PATH_TOKEN } from '../../task-notes.provider';
import { NOTES_LIST_PATH } from '../../task-notes.const';

@Component({
  selector: 'app-delete-note-container',
  template: `<app-delete-note (handleDelete)="onDelete()" />`,
  standalone: true,
  imports: [DeleteNoteComponent],
})
export class DeleteNoteContainerComponent {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);
  private readonly DETAILS_PATH = inject(TASK_NOTES_PARENT_DETAILS_PATH_TOKEN);

  constructor() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${this.DETAILS_PATH}/${this.route.snapshot.paramMap.get('requestId')}/${NOTES_LIST_PATH}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onDelete() {
    this.store.dispatch(TaskNotesActions.DELETE_NOTE());
  }
}
