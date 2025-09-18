import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  concatMap,
  filter,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import {
  acceptAllCookies,
  canGoBack,
  clearErrors,
  clearSubMenu,
  cookiesAccepted,
  cookiesExist,
  downloadEmailsFile,
  errors,
  loadCountries,
  loadCountriesRequested,
  loadNavMenuPermissions,
  loadNavMenuPermissionsSuccess,
  loadRegistrationConfiguration,
  loadRegistrationConfigurationRequested,
  loadRegistryConfiguration,
  loadRegistryConfigurationRequested,
  loadRequestAllocationData,
  loadRequestAllocationDataFailure,
  loadRequestAllocationDataSuccess,
  navigateTo,
  navigateToUserProfile,
  retrieveUserStatus,
  retrieveUserStatusError,
  retrieveUserStatusSuccess,
  setActiveMenus,
} from '@shared/shared.action';
import { ConfigurationService } from '@shared/configuration/configuration.service';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { ErrorDetail, ErrorSummary } from './error-summary';
import { ROUTER_NAVIGATED, RouterNavigatedAction } from '@ngrx/router-store';
import { select, Store } from '@ngrx/store';
import {
  selectAllMenuItems,
  selectAllSubMenuItems,
  selectBooleanConfigurationProperty,
  selectErrorSummary,
  selectUserStatus,
} from '@shared/shared.selector';
import { CountryService } from '@shared/countries/country.service';
import { Router } from '@angular/router';
import { ApiErrorHandlingService, CookieService } from '@shared/services';
import { MenuItem } from '@shared/model/navigation-menu';
import { NavMenuService } from '@shared/services/nav-menu.service';
import {
  isAdmin,
  isAuthenticated,
  selectUrid,
} from '@registry-web/auth/auth.selector';
import { of } from 'rxjs';
import {
  uploadAllocationTableFileSuccess,
  uploadBulkARFileSuccess,
  uploadEmissionsTableFileSuccess,
  uploadRecipientsEmailFileSuccess,
  uploadReportPublicationFileSuccess,
  uploadSelectedFileError,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress,
} from '@shared/file/actions/file-upload-api.actions';
import { BaseType, FileBase, UploadStatus } from '@shared/model/file';
import { UserDetailService } from '@user-management/service';
import { UserStatus } from '@shared/user';
import { RequestAllocationService } from '@registry-web/ets-administration/request-allocation/services/request-allocation.service';
import { RecoveryMethodsChangeService } from '@user-management/recovery-methods-change/recovery-methods-change.service';
import { NotificationApiService } from '@shared/components/notifications/services';
import { ExportFileService } from '@shared/export-file/export-file.service';

@Injectable()
export class SharedEffects {
  cookiesExist$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cookiesExist),
      map(() => {
        return cookiesAccepted({
          acceptCookies: !this.cookieService.notAccepted(),
          browserCookiesEnabled: this.cookieService.checkIfCookiesEnabled(),
        });
      })
    );
  });
  acceptCookies$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(acceptAllCookies),
      map((action) => {
        const acceptCookies = this.cookieService.acceptAllCookies(
          Number(action['expirationTime'])
        );
        return cookiesAccepted({
          acceptCookies: acceptCookies,
          browserCookiesEnabled: this.cookieService.checkIfCookiesEnabled(),
        });
      })
    );
  });

  loadCountriesRequested$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadCountriesRequested),
      mergeMap(() =>
        this.countryService
          .getCountries()
          .pipe(map((result) => loadCountries({ countries: result })))
      )
    )
  );

  loadRegistryConfigurationRequested$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadRegistryConfigurationRequested),
      mergeMap(() =>
        this.configurationService.getConfigurationRegistry().pipe(
          mergeMap((result) => [
            loadCountriesRequested(),
            loadRegistryConfiguration({ configurationRegistry: result }),
            cookiesExist(),
          ]),
          catchError((httpError: any) => this.handleHttpError(httpError))
        )
      )
    )
  );

  loadRegistrationConfigurationRequested$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadRegistrationConfigurationRequested),
      mergeMap(() =>
        this.configurationService.getConfigurationRegistration().pipe(
          mergeMap((result) => [
            loadRegistrationConfiguration({
              configurationRegistration: result,
            }),
          ]),
          catchError((httpError: any) => this.handleHttpError(httpError))
        )
      )
    )
  );

  /**
   * When changing route clear the error summary if it contain errors.
   */
  clearErrorsIfNeeded$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ROUTER_NAVIGATED),
      withLatestFrom(this.store.pipe(select(selectErrorSummary))),
      filter(([action, errorSummary]) => errorSummary != null),
      map(() => clearErrors())
    )
  );

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  menuPermissions$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadNavMenuPermissions),
      withLatestFrom(
        this.store.pipe(select(isAuthenticated)),
        this.store.pipe(
          select(selectBooleanConfigurationProperty, {
            property: 'account.opening.only.for.registry.administration',
          })
        ),
        this.store.pipe(select(selectUserStatus)),
        this.store.pipe(select(isAdmin))
      ),
      switchMap(
        ([_, isUserAuthenticated, isSeniorOrJuniorAdm, userState, isAdmin]) => {
          return this.navMenuService
            .loadMenuPermissions(
              isUserAuthenticated,
              isSeniorOrJuniorAdm,
              userState === 'ENROLLED',
              isAdmin
            )
            .pipe(
              map((scopes) => {
                return loadNavMenuPermissionsSuccess({ scopes });
              }),
              catchError((httpError: any) => this.handleHttpError(httpError))
            );
        }
      )
    )
  );

  loadRequestAllocations$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(loadRequestAllocationData),
      concatMap(() =>
        this.requestAllocationService.getAllocationYears().pipe(
          map((allocationYears) =>
            loadRequestAllocationDataSuccess({
              allocationYears,
            })
          ),
          catchError((error) =>
            of(
              loadRequestAllocationDataFailure({
                error,
              })
            )
          )
        )
      )
    );
  });

  retrieveUserStatus$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(retrieveUserStatus),
      withLatestFrom(this.store.select(selectUrid)),
      switchMap(([_, urid]) => {
        return this.userDetailsService.getUserDetail(urid).pipe(
          map((user) => {
            if (!user) {
              return null;
            }
            const userStatus = user.attributes.state[0] as UserStatus;
            return retrieveUserStatusSuccess({ userStatus });
          })
        );
      }),
      catchError((err) => of(retrieveUserStatusError(err)))
    );
  });

  /**
   * Filters menu-related navigation actions.
   * Sets the selected menu/submenu according to the route.
   * If the route is not menu-related clear the submenu.
   */
  manageActiveMenus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ROUTER_NAVIGATED),
      withLatestFrom(
        this.store.pipe(select(selectAllMenuItems)),
        this.store.pipe(select(selectAllSubMenuItems))
      ),
      map(
        ([route, menuItems, subMenuItems]: [
          RouterNavigatedAction,
          MenuItem[],
          MenuItem[],
        ]) => {
          if (
            !this.navMenuService.isNavigatedRouteAMenuRoute(
              menuItems,
              subMenuItems,
              route.payload.routerState.url
            ) ||
            this.navMenuService.routeUrlBelongIn(route.payload.routerState.url)
          ) {
            return clearSubMenu();
          }
          const { topMenuActive, subMenuActive } =
            this.navMenuService.getActiveMenuItems(
              menuItems,
              subMenuItems,
              route.payload.routerState.url
            );
          return setActiveMenus({ topMenuActive, subMenuActive });
        }
      )
    )
  );

  // TODO: back route is not used in user details page
  navigateToUserProfile$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToUserProfile),
      concatMap((action) => [
        canGoBack({
          goBackRoute: action.goBackRoute,
        }),
        navigateTo({
          route: action.userProfileRoute,
        }),
      ])
    );
  });

  downloadFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(downloadEmailsFile),
        mergeMap((action) => {
          return this.notificationApiService
            .downloadRecipientsEmailsFile(action.fileId)
            .pipe(
              map((result) => {
                this.exportFileService.export(
                  result.body,
                  this.exportFileService.getContentDispositionFilename(
                    result.headers.get('Content-Disposition')
                  )
                );
              }),
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
    },
    { dispatch: false }
  );

  manageFileUpload(event: HttpEvent<any>) {
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
        if (event.body?.baseType === BaseType.ALLOCATION_TABLE) {
          return of(
            uploadAllocationTableFileSuccess({
              fileHeader: event.body,
            })
          );
        }
        if (event.body?.baseType === BaseType.BULK_AR) {
          return of(
            uploadBulkARFileSuccess({
              fileHeader: event.body,
            })
          );
        }
        if (event.body?.baseType === BaseType.EMISSIONS_TABLE) {
          return of(
            uploadEmissionsTableFileSuccess({
              fileHeader: event.body,
            })
          );
        }
        if (event.body?.baseType === BaseType.PUBLICATION_REPORT) {
          return of(
            uploadReportPublicationFileSuccess({
              fileHeader: event.body,
            })
          );
        }
        if (event.body?.baseType === BaseType.AD_HOC_EMAIL_RECIPIENTS) {
          return of(
            uploadRecipientsEmailFileSuccess({
              fileHeader: event.body,
            })
          );
        }
        break;
      default:
        return of(
          uploadSelectedFileError({
            status: UploadStatus.Failed,
          })
        );
    }
  }

  private handleHttpError(httpError: HttpErrorResponse) {
    return [
      errors({
        errorSummary: new ErrorSummary([
          new ErrorDetail(null, httpError.error),
        ]),
      }),
    ];
  }

  constructor(
    private actions$: Actions,
    private countryService: CountryService,
    private cookieService: CookieService,
    private configurationService: ConfigurationService,
    private store: Store,
    private router: Router,
    private navMenuService: NavMenuService,
    private userDetailsService: UserDetailService,
    private requestAllocationService: RequestAllocationService,
    private recoveryMethodsService: RecoveryMethodsChangeService,
    private notificationApiService: NotificationApiService,
    private exportFileService: ExportFileService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}
}
