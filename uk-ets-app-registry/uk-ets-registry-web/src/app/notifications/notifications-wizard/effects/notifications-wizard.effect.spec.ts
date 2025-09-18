import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHandler } from '@angular/common/http';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { TestScheduler } from 'rxjs/testing';
import { NotificationsWizardEffect } from './notifications-wizard.effect';
import { NotificationApiService } from '@registry-web/shared/components/notifications/services';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { NotificationsWizardActions } from '../actions';
import { SharedEffects } from '@registry-web/shared/shared.effect';
import { RouterTestingModule } from '@angular/router/testing';
import { Action } from '@ngrx/store';
import { NotificationsWizardState } from '../reducers';
import { UK_ETS_REGISTRY_API_BASE_URL, USER_REGISTRATION_SERVICE_URL } from '@registry-web/app.tokens';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { KeycloakService } from 'keycloak-angular';
import { ExportFileService } from '@registry-web/shared/export-file/export-file.service';
import { cold, hot } from 'jasmine-marbles';
import { ErrorSummary } from '@registry-web/shared/error-summary';
import { errors } from '@registry-web/shared/shared.action';

describe('NotificationsWizardEffect', () => {
  let actions$ = new Observable<Action>();
  let store: MockStore<NotificationsWizardState>;
  
  let effects: NotificationsWizardEffect;
  let notificationApiService: NotificationApiService;
  let apiErrorHandlingService: ApiErrorHandlingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        // imports:[RouterTestingModule, SharedEffects],
        providers: [
            NotificationsWizardEffect,
            provideMockStore(),
            provideMockActions(() => actions$),
            NotificationApiService,
            ApiErrorHandlingService,
            SharedEffects,
            RouterTestingModule,
            HttpClient,
            HttpHandler,
	        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'ets-reg' },	    
	        { provide: USER_REGISTRATION_SERVICE_URL, useValue: 'user-reg' },
            AuthApiService,
            KeycloakService,
            ExportFileService	    
        ],
    }).compileComponents();

    effects = TestBed.inject(NotificationsWizardEffect);
    store = TestBed.inject(MockStore);
    notificationApiService = TestBed.inject(NotificationApiService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);    
  });


  test('should create NotificationsWizardEffect', () => {
    expect(effects).toBeTruthy();
  });

  describe('cancelActiveOrPendingNotificationClicked$', () => {
    test('should dispatch cancelActiveOrPendingNotificationSuccess when API call succeeds', () => {
        const notificationId = '123';
        const action = NotificationsWizardActions.cancelActiveOrPendingNotification({ notificationId });
        const successAction = NotificationsWizardActions.cancelActiveOrPendingNotificationSuccess({ notificationId });

        jest
        .spyOn(notificationApiService, 'cancelActiveOrPendingNotification')
        .mockReturnValue(cold('-b', { b: {} }));

        actions$ = hot('-a', { a: action });

        const expected = '--c';
        const expectedValues = { c: successAction };

        effects.cancelActiveOrPendingNotificationClicked$.subscribe(res => {
            expect(res).toBe(expectedValues);
        });


        // expect(notificationApiService.cancelActiveOrPendingNotification).toHaveBeenCalledWith(notificationId);
    });

    test('should dispatch errors action when API call fails', () => {
        const notificationId = '123';
        const action = NotificationsWizardActions.cancelActiveOrPendingNotification({ notificationId });
        const errorResponse = new HttpErrorResponse({
            error: { message: 'Error occurred' },
            status: 500,
        });
        const errorSummary = apiErrorHandlingService.transform(errorResponse.error);
        const errorAction = errors({errorSummary});

        apiErrorHandlingService.transform = jest.fn(() => errorSummary);
        const apiErrorHandlingServiceSpy = jest.spyOn(
            apiErrorHandlingService,
            'transform'
        );
      
        actions$ = hot('-a', { a: action });
        const serviceResponse = cold('-#', {}, errorResponse);
        const expectedEffectResponse = cold('-b', { b: errorAction });

      expect(effects.cancelActiveOrPendingNotificationClicked$).toBeObservable(
        expectedEffectResponse
      );

      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });

  describe('navigateToNotificationCancelled$', () => {
    test('should dispatch navigateTo action with correct route', () => {
        const notificationId = '123';
        const action = NotificationsWizardActions.cancelActiveOrPendingNotificationSuccess({ notificationId });
        const path = `/notifications/${notificationId}/base-path/notification-cancelled`;
        const navigateAction = NotificationsWizardActions.navigateTo({ 
            route: path, 
            extras: { skipLocationChange: true } 
        });

        actions$ = hot('-a', { a: action });

        const expected = '-b';
        const expectedValues = { b: navigateAction };
        effects.navigateToNotificationCancelled$.subscribe(res => {
            expect(res).toBe(navigateAction);
        });
    });
  });
});
