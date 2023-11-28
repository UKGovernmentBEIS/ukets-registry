import { Injectable } from '@angular/core';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { catchError, filter, map, mergeMap, switchMap, tap } from 'rxjs';
import * as AccountNotesActions from './notes.actions';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { Router } from '@angular/router';
import {
  selectAddNotesDescription,
  selectAddNotesType,
  selectDeleteNoteId,
} from './notes.selector';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorSummary, ErrorDetail } from '@registry-web/shared/error-summary';
import { errors } from '@registry-web/shared/shared.action';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { NoteType } from '@registry-web/shared/model/note';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model';
import { NotesApiService } from '../services/notes-api.service';

@Injectable()
export class NotesEffect {
  constructor(
    private actions$: Actions,
    private router: Router,
    private store: Store,
    private notesService: NotesApiService
  ) {}

  fetchAcountNotes$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountNotesActions.fetchNotes),
      concatLatestFrom(() => this.store.select(isAdmin)),
      filter(([, isAdmin]) => isAdmin),
      switchMap(([payload]) => {
        return this.notesService.fetchAccountNotes(payload.domainId).pipe(
          map((response) =>
            AccountNotesActions.fetchNotesSuccess({ response })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        );
      })
    );
  });

  createNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountNotesActions.createNote),
      concatLatestFrom(() => [
        this.store.select(selectAddNotesDescription),
        this.store.select(selectAddNotesType),
        this.store.select(selectAccount),
      ]),
      mergeMap(([, description, type, account]) => {
        const domainId =
          type === NoteType.ACCOUNT_HOLDER
            ? String(account.accountHolder.id)
            : String(account.identifier);

        return this.notesService
          .createNote({
            description,
            domainType: NoteType[type],
            domainId,
          })
          .pipe(
            map((response) =>
              AccountNotesActions.createNoteSuccess({ response })
            ),
            catchError((httpError) => this.handleHttpError(httpError))
          );
      })
    );
  });

  deleteNote$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountNotesActions.deleteNote),
      concatLatestFrom(() => this.store.select(selectDeleteNoteId)),
      mergeMap(([, noteId]) => {
        return this.notesService.deleteNote(noteId).pipe(
          map((response) =>
            AccountNotesActions.deleteNoteSuccess({ response })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        );
      })
    );
  });

  navigateToAccountNotes$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountNotesActions.navigateToAccountNotes),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) => {
          this.router.navigate([`/account/${account.identifier}`], {
            skipLocationChange: true,
            queryParams: { selectedSideMenu: MenuItemEnum.NOTES },
          });
        })
      );
    },
    { dispatch: false }
  );

  navigateToCancelAddNote$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountNotesActions.navigateCancelAddNote),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([action, account]) => {
          this.router.navigate(
            [`/account/${account.identifier}/cancel-add-note`],
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

  navigateToSelectEntity$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountNotesActions.navigateSelectEntity),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate(
            [`/account/${account.identifier}/select-entity`],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToAddNote$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountNotesActions.navigateAddNote),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate([`/account/${account.identifier}/add-note`], {
            skipLocationChange: true,
          })
        )
      );
    },
    { dispatch: false }
  );

  navigateToCheckAndConfirm$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountNotesActions.navigateCheckAndConfirm),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate(
            [`/account/${account.identifier}/check-and-confirm-add-note`],
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
        ofType(AccountNotesActions.createNoteSuccess),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate(
            [`/account/${account.identifier}/add-note-success`],
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
      ofType(AccountNotesActions.navigateDeleteNote),
      concatLatestFrom(() => this.store.select(selectAccount)),
      tap(([, account]) =>
        this.router.navigate([`/account/${account.identifier}/delete-note`], {
          skipLocationChange: true,
        })
      ),
      map(([payload]) =>
        AccountNotesActions.saveDeleteNoteId({ noteId: payload.noteId })
      )
    );
  });

  navigateToDeleteNoteSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountNotesActions.deleteNoteSuccess),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate(
            [`/account/${account.identifier}/delete-note-success`],
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
      ofType(AccountNotesActions.cancelAddNote),
      concatLatestFrom(() => this.store.select(selectAccount)),
      tap(([, account]) =>
        this.router.navigate([`/account/${account.identifier}`], {
          skipLocationChange: true,
        })
      ),
      map(() => AccountNotesActions.clearNoteState())
    );
  });

  private handleHttpError(httpError: HttpErrorResponse) {
    return [
      errors({
        errorSummary: new ErrorSummary([
          new ErrorDetail(null, httpError.error),
        ]),
      }),
    ];
  }
}
