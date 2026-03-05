import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { catchError, filter, map, mergeMap, switchMap, tap } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@registry-web/shared/shared.action';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { NoteType } from '@registry-web/shared/model/note';
import { NotesApiService } from '@notes/services/notes-api.service';
import { ApiErrorHandlingService } from '@registry-web/shared/services/api-error-handling.service';
import { NotesWizardPathsModel } from '@registry-web/notes/model/notes-wizard-paths.model';

import { TASK_NOTES_PARENT_DETAILS_PATH_TOKEN } from '../task-notes.provider';
import { TaskNotesActions } from './task-notes.actions';
import {
  selectAddNotesDescription,
  selectDeleteNoteId,
  selectDomainId,
} from './task-notes.selector';
import { NOTES_LIST_PATH } from '../task-notes.const';

@Injectable()
export class TaskNotesEffects {
  private readonly DETAILS_PATH = inject(TASK_NOTES_PARENT_DETAILS_PATH_TOKEN);

  constructor(
    private actions$: Actions,
    private router: Router,
    private store: Store,
    private notesService: NotesApiService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}

  fetchTaskNotes$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.FETCH_TASK_NOTES),
      concatLatestFrom(() => this.store.select(isAdmin)),
      filter(([, isAdmin]) => isAdmin),
      switchMap(([payload]) => {
        return this.notesService
          .fetchNotes(payload.requestId, NoteType.TASK)
          .pipe(
            map((response) =>
              TaskNotesActions.FETCH_TASK_NOTES_SUCCESS({ response })
            ),
            catchError((httpError) => this.handleHttpError(httpError))
          );
      })
    );
  });

  createNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.CREATE_NOTE),
      concatLatestFrom(() => [
        this.store.select(selectAddNotesDescription),
        this.store.select(selectDomainId),
      ]),
      mergeMap(([, description, domainId]) => {
        return this.notesService
          .createNote({
            description,
            domainType: NoteType[NoteType.TASK],
            domainId: String(domainId),
          })
          .pipe(
            map((response) =>
              TaskNotesActions.CREATE_NOTE_SUCCESS({ response })
            ),
            catchError((httpError) => this.handleHttpError(httpError))
          );
      })
    );
  });

  deleteNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.DELETE_NOTE),
      concatLatestFrom(() => this.store.select(selectDeleteNoteId)),
      mergeMap(([, noteId]) => {
        return this.notesService.deleteNote(noteId).pipe(
          map((response) => TaskNotesActions.DELETE_NOTE_SUCCESS({ response })),
          catchError((httpError) => this.handleHttpError(httpError))
        );
      })
    );
  });

  navigateToTaskNotes$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.NAVIGATE_TO_TASK_NOTES),
        concatLatestFrom(() => this.store.select(selectDomainId)),
        tap(([, domainId]) => {
          this.router.navigate(
            [`/${this.DETAILS_PATH}/${domainId}/${NOTES_LIST_PATH}`],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  navigateToCancelAddNote$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.NAVIGATE_TO_CANCEL_ADD_NOTE),
        concatLatestFrom(() => this.store.select(selectDomainId)),
        tap(([action, domainId]) => {
          this.router.navigate(
            [
              `/${this.DETAILS_PATH}/${domainId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.CANCEL_ADD_NOTE}`,
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
        ofType(TaskNotesActions.NAVIGATE_TO_ADD_NOTE),
        concatLatestFrom(() => this.store.select(selectDomainId)),
        tap(([, domainId]) =>
          this.router.navigate(
            [
              `/${this.DETAILS_PATH}/${domainId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.ADD_NOTE}`,
            ],
            { skipLocationChange: true }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToCheckAndConfirm$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.NAVIGATE_TO_CHECK_AND_CONFIRM),
        concatLatestFrom(() => this.store.select(selectDomainId)),
        tap(([, domainId]) =>
          this.router.navigate(
            [
              `/${this.DETAILS_PATH}/${domainId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.CHECK_AND_CONFIRM_ADD_NOTE}`,
            ],
            { skipLocationChange: true }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToAddNoteSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.CREATE_NOTE_SUCCESS),
        concatLatestFrom(() => this.store.select(selectDomainId)),
        tap(([, domainId]) =>
          this.router.navigate(
            [
              `/${this.DETAILS_PATH}/${domainId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.ADD_NOTE_SUCCESS}`,
            ],
            { skipLocationChange: true }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToDeleteNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.NAVIGATE_TO_DELETE_NOTE),
      concatLatestFrom(() => this.store.select(selectDomainId)),
      tap(([, domainId]) =>
        this.router.navigate(
          [
            `/${this.DETAILS_PATH}/${domainId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.DELETE_NOTE}`,
          ],
          { skipLocationChange: true }
        )
      ),
      map(([payload]) =>
        TaskNotesActions.SAVE_DELETE_NOTE_ID({ noteId: payload.noteId })
      )
    );
  });

  navigateToDeleteNoteSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskNotesActions.DELETE_NOTE_SUCCESS),
        concatLatestFrom(() => this.store.select(selectDomainId)),
        tap(([, domainId]) =>
          this.router.navigate(
            [
              `/${this.DETAILS_PATH}/${domainId}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.DELETE_NOTE_SUCCESS}`,
            ],
            { skipLocationChange: true }
          )
        )
      );
    },
    { dispatch: false }
  );

  cancelAddNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskNotesActions.CANCEL_ADD_NOTE),
      concatLatestFrom(() => this.store.select(selectDomainId)),
      tap(([, domainId]) =>
        this.router.navigate(
          [`/${this.DETAILS_PATH}/${domainId}/${NOTES_LIST_PATH}`],
          {
            skipLocationChange: true,
          }
        )
      ),
      map(() => TaskNotesActions.CLEAR_FORM_STATE())
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
