import { Observable } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { cold, hot } from 'jasmine-marbles';
import { errors } from '@shared/shared.action';
import {
  navigateToVerificationPage,
  requestEmailChangeAction
} from '@email-change/action/email-change.actions';
import {
  EmailChange,
  EmailChangeRequest
} from '@email-change/model/email-change.model';
import { EmailChangeEffect } from '@email-change/effect/email-change.effects';
import { ErrorSummary } from '@shared/error-summary';
import { HttpErrorResponse } from '@angular/common/http';
import { selectState } from '@email-change/reducer/email-change.selector';
import { ApiErrorHandlingService } from '@shared/services';
import { RequestEmailChangeService } from '@email-change/service/request-email-change.service';
import { EmailChangeState } from '@email-change/reducer';

describe('emailChangeEffect', () => {
  let actions: Observable<any>;
  let effects: EmailChangeEffect;
  let mockStore: MockStore<EmailChangeState>;
  let apiErrorHandlingService: ApiErrorHandlingService;
  let requestEmailChangeService: RequestEmailChangeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EmailChangeEffect,
        ApiErrorHandlingService,
        provideMockStore(),
        provideMockActions(() => actions),
        {
          provide: RequestEmailChangeService,
          useValue: {
            changeEmail: jest.fn()
          }
        }
      ]
    });
    effects = TestBed.inject(EmailChangeEffect);
    mockStore = TestBed.inject(MockStore);
    requestEmailChangeService = TestBed.inject(RequestEmailChangeService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('changeEmail$', () => {
    it('should call the service and return [navigateToVerificationPage]', () => {
      const emailChange: EmailChange = {
        newEmail: 'test_new@test.com',
        urid: 'UK14234'
      };

      const emailChangeReq: EmailChangeRequest = {
        newEmail: emailChange.newEmail,
        otp: '12332'
      };

      const serviceResponse = cold('-a|', {
        a: emailChange
      });
      requestEmailChangeService.changeEmail = jest.fn(
        (request: EmailChangeRequest) => {
          expect(request.newEmail).toBe(emailChangeReq.newEmail);
          expect(request.otp).toBe(emailChangeReq.otp);

          return serviceResponse;
        }
      );
      actions = hot('-a', {
        a: requestEmailChangeAction({
          request: emailChangeReq
        })
      });

      const expectedEffectResponse = cold('--(b)', {
        b: navigateToVerificationPage({
          newEmail: emailChangeReq.newEmail
        })
      });

      expect(effects.changeEmail$).toBeObservable(expectedEffectResponse);
    });

    it('should return errors action, with the ErrorSummary transformed by apiErrorHandlingService, on failure', () => {
      const emailChange: EmailChangeState = {
        newEmail: 'test_new@test.com',
        urid: 'UK14234',
        caller: null,
        confirmationLoaded: false,
        confirmation: null
      };
      mockStore.overrideSelector(selectState, emailChange);
      const errorSummary = new ErrorSummary([
        { errorMessage: 'test dummy error', componentId: '' }
      ]);
      const errorsAction = errors({ errorSummary });
      apiErrorHandlingService.transform = jest.fn(() => errorSummary);
      const apiErrorHandlingServiceSpy = jest.spyOn(
        apiErrorHandlingService,
        'transform'
      );
      const httpErrorResponse = new HttpErrorResponse({});
      actions = hot('-a', {
        a: requestEmailChangeAction({
          request: {
            newEmail: 'test@test.test',
            otp: '123'
          }
        })
      });

      const serviceResponse = cold('-#', {}, httpErrorResponse);
      const expectedEffectResponse = cold('--b', { b: errorsAction });
      requestEmailChangeService.changeEmail = jest.fn(() => serviceResponse);
      expect(effects.changeEmail$).toBeObservable(expectedEffectResponse);
      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });
});
