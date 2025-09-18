import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { catchError, filter, map, mergeMap, switchMap, tap } from 'rxjs';
import * as TaskNotesActions from './task-notes.actions';
import { Router } from '@angular/router';
import {
  selectAddNotesDescription,
  selectDeleteNoteId,
} from './task-notes.selector';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorSummary, ErrorDetail } from '@registry-web/shared/error-summary';
import { errors } from '@registry-web/shared/shared.action';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { NoteType } from '@registry-web/shared/model/note';
import { NotesApiService } from '@notes/services/notes-api.service';
import { NotesWizardPathsModel } from '@registry-web/notes/model/notes-wizard-paths.model';
import { selectTask } from '@registry-web/task-management/task-details/reducers/task-details.selector';
import { ApiErrorHandlingService } from '@registry-web/shared/services/api-error-handling.service';

@Injectable()
export class NotesEffect {
  constructor(
    private actions$: Actions,
    private router: Router,
    private store: Store,
    private notesService: NotesApiService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}

  fetchTaskNotes$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.fetchTaskNotes),
      concatLatestFrom(() => this.store.select(isAdmin)),
      filter(([, isAdmin]) => isAdmin),
      switchMap(([payload]) => {
        return this.notesService
          .fetchNotes(payload.requestId, NoteType.TASK)
          .pipe(
            map((response) =>
              TaskNotesActions.fetchTaskNotesSuccess({ response })
            ),
            catchError((httpError) => this.handleHttpError(httpError))
          );
      })
    );
  });

  createNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.createNote),
      concatLatestFrom(() => [
        this.store.select(selectAddNotesDescription),
        this.store.select(selectTask),
      ]),
      mergeMap(([, description, task]) => {
        const domainId = task.requestId;

        return this.notesService
          .createNote({
            description,
            domainType: NoteType[NoteType.TASK],
            domainId: String(domainId),
          })
          .pipe(
            map((response) => TaskNotesActions.createNoteSuccess({ response })),
            catchError((httpError) => this.handleHttpError(httpError))
          );
      })
    );
  });

  deleteNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.deleteNote),
      concatLatestFrom(() => this.store.select(selectDeleteNoteId)),
      mergeMap(([, noteId]) => {
        return this.notesService.deleteNote(noteId).pipe(
          map((response) => TaskNotesActions.deleteNoteSuccess({ response })),
          catchError((httpError) => this.handleHttpError(httpError))
        );
      })
    );
  });

  navigateToTaskNotes$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.navigateToTaskNotes),
        concatLatestFrom(() => this.store.select(selectTask)),
        tap(([, task]) => {
          this.router.navigate([`/task-details/${task.requestId}/notes-list`], {
            skipLocationChange: true,
          });
        })
      );
    },
    { dispatch: false }
  );

  navigateToCancelAddNote$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.navigateCancelAddNote),
        concatLatestFrom(() => this.store.select(selectTask)),
        tap(([action, task]) => {
          this.router.navigate(
            [
              `/task-details/${task.requestId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.CANCEL_ADD_NOTE}`,
            ],
            {
              skipLocationChange: true,
              queryParams: {
                goBackRoute: action.currentRoute,
              },
            }
          );
        })
      );
    },
    { dispatch: false }
  );

  navigateToAddNote$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.navigateAddNote),
        concatLatestFrom(() => this.store.select(selectTask)),
        tap(([, task]) =>
          this.router.navigate(
            [
              `/task-details/${task.requestId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.ADD_NOTE}`,
            ],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToCheckAndConfirm$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.navigateCheckAndConfirm),
        concatLatestFrom(() => this.store.select(selectTask)),
        tap(([, task]) =>
          this.router.navigate(
            [
              `/task-details/${task.requestId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.CHECK_AND_CONFIRM_ADD_NOTE}`,
            ],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToAddNoteSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.createNoteSuccess),
        concatLatestFrom(() => this.store.select(selectTask)),
        tap(([, task]) =>
          this.router.navigate(
            [
              `/task-details/${task.requestId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.ADD_NOTE_SUCCESS}`,
            ],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToDeleteNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.navigateDeleteNote),
      concatLatestFrom(() => this.store.select(selectTask)),
      tap(([, task]) =>
        this.router.navigate(
          [
            `/task-details/${task.requestId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.DELETE_NOTE}`,
          ],
          {
            skipLocationChange: true,
          }
        )
      ),
      map(([payload]) =>
        TaskNotesActions.saveDeleteNoteId({ noteId: payload.noteId })
      )
    );
  });

  navigateToDeleteNoteSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.deleteNoteSuccess),
        concatLatestFrom(() => this.store.select(selectTask)),
        tap(([, task]) =>
          this.router.navigate(
            [
              `/task-details/${task.requestId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.DELETE_NOTE_SUCCESS}`,
            ],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  cancelAddNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.cancelAddNote),
      concatLatestFrom(() => this.store.select(selectTask)),
      tap(([, task]) =>
        this.router.navigate([`/task-details/${task.requestId}/notes-list`], {
          skipLocationChange: true,
        })
      ),
      map(() => TaskNotesActions.clearNoteState())
    );
  });

  private handleHttpError(httpError: HttpErrorResponse) {
    return [
      errors({
        errorSummary: this.apiErrorHandlingService.transform(httpError.error),
      }),
    ];
  }
}
