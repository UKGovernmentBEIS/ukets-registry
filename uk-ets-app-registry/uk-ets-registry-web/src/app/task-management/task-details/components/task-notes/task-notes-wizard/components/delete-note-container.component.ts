import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import * as TaskNotesActions from '../../store/task-notes.actions';
import { DeleteNoteComponent } from '@notes/components/delete-note/delete-note.component';

@Component({
  standalone: true,
  imports: [DeleteNoteComponent],
  selector: 'app-delete-note-container',
  template: ` <app-delete-note (handleDelete)="onDelete()"></app-delete-note>`,
})
export class DeleteNoteContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}`,
        extras: {
          skipLocationChange: true,
        },
      })
    );
  }

  onDelete() {
    this.store.dispatch(TaskNotesActions.deleteNote());
  }
}
