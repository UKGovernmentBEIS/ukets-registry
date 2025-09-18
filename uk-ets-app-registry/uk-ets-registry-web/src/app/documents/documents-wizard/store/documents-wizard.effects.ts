import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { DocumentsApiService } from '@registry-web/documents/service/documents-api.service';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { errors } from '@registry-web/shared/shared.action';
import {
  tap,
  switchMap,
  map,
  catchError,
  of,
  mergeMap,
  filter,
  delay,
} from 'rxjs';
import * as DocumentsWizardActions from './documents-wizard.actions';
import * as DocumentsActions from '../../store/documents.actions';

import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import {
  selectDocument,
  selectDocumentCategory,
  selectFileUpdated,
  selectUpdateType,
} from './documents-wizard.selector';
import {
  processSelectedFileError,
  uploadSelectedFileError,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress,
} from '@registry-web/shared/file/actions/file-upload-api.actions';
import { UploadStatus } from '@registry-web/shared/model/file';
import { TaskDetailsActions } from '@registry-web/task-management/task-details/actions';
import {
  selectTask,
  selectUploadedFileDetails,
} from '@registry-web/task-management/task-details/reducers/task-details.selector';
import { selectCategories } from '@registry-web/documents/store/documents.selectors';
import { ExportFileService } from '@registry-web/shared/export-file/export-file.service';

@Injectable()
export class DocumentsWizardEffects {
  constructor(
    private actions$: Actions,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private store: Store,
    private exportFileService: ExportFileService,
    private documentsApiService: DocumentsApiService
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.navigateTo),
        tap((action) => this.router.navigate([action.route], action.extras))
      );
    },
    { dispatch: false }
  );

  navigateToSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.navigateToSuccess),
        tap(() =>
          this.router.navigate(
            [`${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.SUCCESS}`],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToUploadDocumentUpdateRequest$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.navigateToUploadDocumentUpdateRequest),
        tap(() =>
          this.router.navigate(
            [
              `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.UPLOAD_DOCUMENT}`,
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

  navigateToUpdateDocumentCategoryDetails$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.navigateToUpdateDocumentCategoryDetails),
        tap(() =>
          this.router.navigate(
            [
              `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.UPDATE_DOCUMENT_CATEGORY_DETAILS}`,
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

  navigateToChooseDisplayOrder$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.navigateToChooseDisplayOrder),
        tap(() =>
          this.router.navigate(
            [
              `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.CHOOSE_DISPLAY_ORDER}`,
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

  navigateToAddDocument$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.navigateToAddDocument),
        tap(() =>
          this.router.navigate(
            [
              `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.ADD_DOCUMENT}`,
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

  navigateToCancel$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.navigateToCancelDocumentUpdateRequest),
        tap((action) => {
          this.router.navigate(
            [
              `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.CANCEL_DOCUMENT_UPDATE}`,
            ],
            {
              skipLocationChange: true,
              queryParams: {
                goBackRoute: action.route,
              },
            }
          );
        })
      );
    },
    { dispatch: false }
  );

  navigateToCheckAndSubmit$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          DocumentsWizardActions.navigateToCheckAndSubmitDocumentUpdateRequest
        ),
        tap((action) => {
          this.router.navigate(
            [
              `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.CHECK_UPDATE_AND_SUBMIT}`,
            ],
            {
              skipLocationChange: true,
            }
          );
        })
      );
    },
    { dispatch: false }
  );

  addDocumentCategory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.addDocumentCategory),
      map((action) =>
        DocumentsWizardActions.navigateToCheckAndSubmitDocumentUpdateRequest()
      )
    );
  });

  setUpdateType$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.setUpdateType),
        map(({ selectedUpdateType }) => {
          switch (selectedUpdateType) {
            case DocumentUpdateType.ADD_DOCUMENT:
              return DocumentsWizardPath.ADD_DOCUMENT;
            case DocumentUpdateType.ADD_DOCUMENT_CATEGORY:
              return DocumentsWizardPath.ADD_DOCUMENT_CATEGORY;
            case DocumentUpdateType.UPDATE_DOCUMENT:
              return DocumentsWizardPath.UPDATE_DOCUMENT;
            case DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY:
              return DocumentsWizardPath.UPDATE_DOCUMENT_CATEGORY;
            case DocumentUpdateType.DELETE_DOCUMENT:
              return DocumentsWizardPath.DELETE_DOCUMENT;
            case DocumentUpdateType.DELETE_DOCUMENT_CATEGORY:
              return DocumentsWizardPath.DELETE_DOCUMENT_CATEGORY;
          }
        }),
        tap((path) =>
          this.router.navigate([`${DocumentsWizardPath.BASE_PATH}/${path}`], {
            skipLocationChange: true,
          })
        )
      );
    },
    { dispatch: false }
  );

  cancelDocumentUpdateRequestConfirm$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.cancelDocumentUpdateRequestConfirm),
        map(() => this.router.navigate([`/documents`]))
      );
    },
    { dispatch: false }
  );

  createCategory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.createCategory),
      concatLatestFrom(() => this.store.select(selectDocumentCategory)),
      mergeMap(([action, documentCategory]) => {
        return this.documentsApiService
          .createDocumentCategory(documentCategory)
          .pipe(
            map((response) => {
              return DocumentsWizardActions.createCategorySuccess({
                response,
              });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  patchDocumentCategory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.patchDocumentCategory),
      map(() =>
        DocumentsWizardActions.navigateToCheckAndSubmitDocumentUpdateRequest()
      )
    );
  });

  createCategorySuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.createCategorySuccess),
      map(() => DocumentsWizardActions.navigateToSuccess())
    );
  });

  updateCategory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.updateCategory),
      concatLatestFrom(() => this.store.select(selectDocumentCategory)),
      mergeMap(([action, documentCategory]) => {
        return this.documentsApiService
          .updateDocumentCategory(documentCategory)
          .pipe(
            map((response) => {
              return DocumentsWizardActions.updateCategorySuccess({
                response,
              });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  updateCategorySuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.updateCategorySuccess),
      map(() => DocumentsWizardActions.navigateToSuccess())
    );
  });

  deleteCategory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.deleteCategory),
      concatLatestFrom(() => this.store.select(selectDocumentCategory)),
      mergeMap(([action, documentCategory]) => {
        return this.documentsApiService
          .deleteDocumentCategory(documentCategory.id)
          .pipe(
            map((response) => {
              return DocumentsWizardActions.deleteCategorySuccess({
                response,
              });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  deleteCategorySuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.deleteCategorySuccess),
      map(() => DocumentsWizardActions.navigateToSuccess())
    );
  });

  deleteDocument$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.deleteDocument),
      concatLatestFrom(() => this.store.select(selectDocument)),
      mergeMap(([action, document]) => {
        return this.documentsApiService
          .deleteDocument(String(document.id))
          .pipe(
            map((response) => {
              return DocumentsWizardActions.deleteDocumentSuccess({
                response,
              });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  deleteDocumentSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.deleteDocumentSuccess),
      map(() => DocumentsWizardActions.navigateToSuccess())
    );
  });

  createDocument$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.createDocument),
      concatLatestFrom(() => this.store.select(selectDocument)),
      switchMap(([action, document]) => {
        return this.documentsApiService.createDocument(document).pipe(
          mergeMap((event: HttpEvent<any>) => {
            switch (event.type) {
              case HttpEventType.Sent:
                return of(
                  uploadSelectedFileHasStarted({
                    status: UploadStatus.Started,
                  })
                );
              case HttpEventType.UploadProgress:
                return of(
                  uploadSelectedFileInProgress({
                    progress: Math.round((100 * event.loaded) / event.total),
                  })
                );
              case HttpEventType.Response:
                return of(
                  DocumentsWizardActions.createDocumentSuccess({
                    response: event,
                  })
                );
              default:
                return of(
                  uploadSelectedFileError({
                    status: UploadStatus.Failed,
                  })
                );
            }
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              processSelectedFileError({
                status: UploadStatus.Failed,
              }),
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            );
          })
        );
      })
    );
  });

  createDocumentSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.createDocumentSuccess),
      map(() => DocumentsWizardActions.navigateToSuccess())
    );
  });

  updateDocument$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.updateDocument),
      concatLatestFrom(() => [
        this.store.select(selectDocument),
        this.store.select(selectFileUpdated),
      ]),
      switchMap(([action, document, fileUpdated]) => {
        const documentBody = { ...document };
        if (!fileUpdated) {
          delete documentBody.file;
        }
        return this.documentsApiService.updateDocument(documentBody).pipe(
          mergeMap((event: HttpEvent<any>) => {
            switch (event.type) {
              case HttpEventType.Sent:
                return of(
                  uploadSelectedFileHasStarted({
                    status: UploadStatus.Started,
                  })
                );
              case HttpEventType.UploadProgress:
                return of(
                  uploadSelectedFileInProgress({
                    progress: Math.round((100 * event.loaded) / event.total),
                  })
                );
              case HttpEventType.Response:
                return of(
                  DocumentsWizardActions.updateDocumentSuccess({
                    response: event,
                  })
                );
              default:
                return of(
                  uploadSelectedFileError({
                    status: UploadStatus.Failed,
                  })
                );
            }
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              processSelectedFileError({
                status: UploadStatus.Failed,
              }),
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            );
          })
        );
      })
    );
  });

  updateDocumentSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.updateDocumentSuccess),
      map(() => DocumentsWizardActions.navigateToSuccess())
    );
  });

  setCategory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.setCategoryId),
      concatLatestFrom(() => [
        this.store.select(selectUpdateType),
        this.store.select(selectCategories),
      ]),
      map(([action, updateType, categories]) => {
        switch (updateType) {
          case DocumentUpdateType.ADD_DOCUMENT:
            return DocumentsWizardActions.navigateToUploadDocumentUpdateRequest();
          case DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY:
            return DocumentsWizardActions.populateDocumentCategoryById({
              selectedCategoryId: action.selectedCategoryId,
              categories,
            });
          case DocumentUpdateType.DELETE_DOCUMENT_CATEGORY:
            return DocumentsWizardActions.populateDocumentCategoryById({
              selectedCategoryId: action.selectedCategoryId,
              categories,
            });
          default:
            return DocumentsWizardActions.navigateToUploadDocumentUpdateRequest();
        }
      })
    );
  });

  setDocument$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.setDocument),
      concatLatestFrom(() => [this.store.select(selectUpdateType)]),
      map(([action, updateType]) => {
        switch (updateType) {
          case DocumentUpdateType.UPDATE_DOCUMENT:
            return DocumentsWizardActions.navigateToUploadDocumentUpdateRequest();
          case DocumentUpdateType.DELETE_DOCUMENT:
            return DocumentsWizardActions.navigateToCheckAndSubmitDocumentUpdateRequest();
          default:
            return DocumentsWizardActions.navigateToUploadDocumentUpdateRequest();
        }
      })
    );
  });

  setFileInState$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.setDocument),
      concatLatestFrom(() => [this.store.select(selectUpdateType)]),
      filter(
        ([action, updateType]) =>
          updateType === DocumentUpdateType.UPDATE_DOCUMENT
      ),
      map(([action, updateType]) =>
        DocumentsWizardActions.populateDocumentFile({
          documentId: action.id,
          filename: action.name,
        })
      )
    );
  });

  populateDocumentCategoryById$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.populateDocumentCategoryById),
      concatLatestFrom(() => this.store.select(selectUpdateType)),
      map(([action, updateType]) => {
        switch (updateType) {
          case DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY:
            return DocumentsWizardActions.navigateToUpdateDocumentCategoryDetails();
          case DocumentUpdateType.DELETE_DOCUMENT_CATEGORY:
            return DocumentsWizardActions.navigateToCheckAndSubmitDocumentUpdateRequest();
          default:
            break;
        }
      })
    );
  });

  setDocumentFile$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.setDocumentFile),
      map((action) => DocumentsWizardActions.navigateToChooseDisplayOrder())
    );
  });

  setDocumentOrder$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.setDocumentOrder),
      map((action) =>
        DocumentsWizardActions.navigateToCheckAndSubmitDocumentUpdateRequest()
      )
    );
  });

  fetchStoredDocumentFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentsWizardActions.fetchStoredDocumentFile),
        concatLatestFrom(() => this.store.select(selectDocument)),
        map(([action, document]) => {
          if (document.file && document.file instanceof File) {
            document.file.arrayBuffer().then((arrayBuffer) => {
              const blob = new Blob([new Uint8Array(arrayBuffer)], {
                type: document.file.type,
              });
              this.exportFileService.export(blob, document.file.name);
            });
          } else {
            return DocumentsActions.fetchDocumentFile({
              documentId: document.id,
            });
          }
        })
      );
    },
    { dispatch: false }
  );

  populateDocumentFile$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentsWizardActions.populateDocumentFile),
      mergeMap((action: { documentId: number; filename: string }) => {
        return this.documentsApiService
          .fetchDocumentFile(action.documentId)
          .pipe(
            map((result) =>
              DocumentsWizardActions.populateDocumentFileSuccess({
                blob: result.body,
                filename: action.filename,
              })
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          );
      })
    );
  });
}
