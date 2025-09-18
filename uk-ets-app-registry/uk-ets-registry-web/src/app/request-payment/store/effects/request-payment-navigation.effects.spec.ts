import { provideMockStore } from '@ngrx/store/testing';
import { RequestPaymentNavigationEffects } from '@request-payment/store/effects/';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';
import { Action } from '@ngrx/store';
import { selectOriginatingPath } from '@request-payment/store/reducers';
import {
  cancelRequestPayment,
  cancelRequestPaymentConfirmed,
  clearRequestPayment,
  enterRequestPaymentWizard,
  setPaymentDetails,
  submitPaymentRequestSuccess,
} from '@request-payment/store/actions';
import { cold, hot } from 'jasmine-marbles';
import { navigateTo } from '@shared/shared.action';

describe('RequestPaymentNavigationEffects', () => {
  let actions: Observable<Action>;
  let effects: RequestPaymentNavigationEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        RequestPaymentNavigationEffects,
        provideMockStore({
          selectors: [
            {
              selector: selectOriginatingPath,
              value: '/task-details/88288990',
            },
          ],
        }),
        provideMockActions(() => actions),
      ],
    });
    effects = TestBed.inject<RequestPaymentNavigationEffects>(
      RequestPaymentNavigationEffects
    );
  });

  test('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('enterRequestPaymentWizard$', () => {
    // Happy path
    test('should navigate to the payments detail page', () => {
      // given
      const enterRequestPaymentWizardAction = enterRequestPaymentWizard({
        origin: 'REPLACE_AUTHORISED_REPRESENTATIVE',
        originatingPath: '/task-details/88288990',
        parentRequestId: '2108821634',
        candidateRecipients: [],
      });
      actions = hot('-a', { a: enterRequestPaymentWizardAction });

      const navigateToAction = navigateTo({
        route: `/request-payment`,
      });
      const expectedEffectResponse = cold('-b', {
        b: navigateToAction,
      });

      // when then
      expect(effects.enterRequestPaymentWizard$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('setPaymentDetails$', () => {
    // Happy path
    test('should navigate to the payments overview page', () => {
      // given
      const setPaymentDetailsAction = setPaymentDetails({
        recipientUrid: 'UK594723414097',
        recipientName: 'A recipient',
        amount: 895.44,
        description: 'A test payment',
      });
      actions = hot('-a', { a: setPaymentDetailsAction });

      const navigateToAction = navigateTo({
        route: `/request-payment/check-payment-request`,
      });
      const expectedEffectResponse = cold('-b', {
        b: navigateToAction,
      });

      // when then
      expect(effects.setPaymentDetails$).toBeObservable(expectedEffectResponse);
    });
  });

  describe('cancelRequestDocuments$', () => {
    // Happy path
    test('should navigate to the cancel request page', () => {
      // given
      const cancelRequestPaymentAction = cancelRequestPayment({
        route: '/request-payment/check-payment-request',
      });
      actions = hot('-a', { a: cancelRequestPaymentAction });

      const navigateToAction = navigateTo({
        route: '/request-payment/cancel-request',
        extras: {
          queryParams: {
            goBackRoute: `/request-payment/check-payment-request`,
          },
          skipLocationChange: true,
        },
      });
      const expectedEffectResponse = cold('-b', {
        b: navigateToAction,
      });

      // when then
      expect(effects.cancelRequestDocuments$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('cancelRequestDocumentsConfirmed$', () => {
    // Happy path
    test('should navigate to the origin request page', () => {
      // given
      const cancelRequestPaymentConfirmedAction =
        cancelRequestPaymentConfirmed();
      actions = hot('-a', { a: cancelRequestPaymentConfirmedAction });

      const navigateToAction = navigateTo({
        route: '/task-details/88288990',
      });
      const clearRequestPaymentAction = clearRequestPayment();
      const expectedEffectResponse = cold('-(bc)', {
        b: clearRequestPaymentAction,
        c: navigateToAction,
      });

      // when then
      expect(effects.cancelRequestDocumentsConfirmed$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('submitPaymentRequestSuccess$', () => {
    // Happy path
    test('should navigate to the cancel request page', () => {
      // given
      const submitPaymentRequestSuccessAction = submitPaymentRequestSuccess({
        submittedRequestIdentifier: '17823737',
      });
      actions = hot('-a', { a: submitPaymentRequestSuccessAction });

      const navigateToAction = navigateTo({
        route: '/request-payment/request-submitted',
        extras: {
          skipLocationChange: true,
        },
      });
      const expectedEffectResponse = cold('-b', {
        b: navigateToAction,
      });

      // when then
      expect(effects.submitPaymentRequestSuccess$).toBeObservable(
        expectedEffectResponse
      );
    });
  });
});
