import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, tap } from 'rxjs';
import { canGoBack } from '@registry-web/shared/shared.action';
import { AddNoteFormComponent } from '@notes/components/add-note-form/add-note-form.component';
import { CommonModule } from '@angular/common';
import { NotesWizardPathsModel } from '@registry-web/notes/model/notes-wizard-paths.model';
import { selectAddNotesDescription } from '../../store/task-notes.selector';
import {
  navigateCancelAddNote,
  navigateCheckAndConfirm,
  saveNoteDescription,
} from '../../store/task-notes.actions';

@Component({
  standalone: true,
  selector: 'app-add-note-form-container',
  imports: [AddNoteFormComponent, CommonModule],
  template: `<app-add-note-form
    [storedNote]="storedNote$ | async"
    (handleSubmit)="onContinue($event)"
    (handleCancel)="onCancel($event)"
  ></app-add-note-form>`,
})
export class AddNoteFormContainerComponent implements OnInit {
  storedNote$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.storedNote$ = this.store
      .select(selectAddNotesDescription)
      .pipe(tap((desc) => console.log(desc)));

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/notes-list`,
        extras: { skipLocationChange: true },
      })
    );
  }

  private saveFormState(noteDescription: string) {
    this.store.dispatch(
      saveNoteDescription({
        noteDescription: noteDescription,
      })
    );
  }

  onContinue(noteDescription: string) {
    this.saveFormState(noteDescription);
    this.store.dispatch(navigateCheckAndConfirm());
  }

  onCancel(noteDescription: string) {
    this.saveFormState(noteDescription);
    this.store.dispatch(
      navigateCancelAddNote({
        currentRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${
          NotesWizardPathsModel.ADD_NOTE
        }`,
      })
    );
  }
}
