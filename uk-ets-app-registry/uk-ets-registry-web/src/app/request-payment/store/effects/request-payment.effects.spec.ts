import { provideMockStore } from '@ngrx/store/testing';
import { RequestPaymentService } from '@request-payment/services';
import { ApiErrorHandlingService } from '@shared/services';
import { RequestPaymentEffects } from '@request-payment/store/effects/';
import { TestBed } from '@angular/core/testing';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';
import { Action } from '@ngrx/store';
import {
  downloadInvoice,
  submitPaymentRequest,
  submitPaymentRequestSuccess,
} from '@request-payment/store/actions';
import { cold, hot } from 'jasmine-marbles';
import { selectPaymentRequest } from '@request-payment/store/reducers';
import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

describe('RequestPaymentEffects', () => {
  let actions: Observable<Action>;
  let effects: RequestPaymentEffects;
  let requestPaymentService: RequestPaymentService;
  let apiErrorHandlingService: ApiErrorHandlingService;
  let exportFileService: ExportFileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        RequestPaymentEffects,
        provideMockStore({
          selectors: [
            {
              selector: selectPaymentRequest,
              value: {
                parentRequestId: '1234567',
                amount: 450.5,
                description: 'A test payment',
                recipientUrid: 'UK367902749814',
              },
            },
          ],
        }),
        provideMockActions(() => actions),
        ApiErrorHandlingService,
        {
          provide: ExportFileService,
          useValue: {
            export: jest.fn(),
            getContentDispositionFilename: jest.fn(),
          },
        },
        {
          provide: RequestPaymentService,
          useValue: {
            submitRequest: jest.fn(),
            downloadInvoice: jest.fn(),
          },
        },
      ],
    });
    effects = TestBed.inject<RequestPaymentEffects>(RequestPaymentEffects);
    requestPaymentService = TestBed.inject<RequestPaymentService>(
      RequestPaymentService
    );
    apiErrorHandlingService = TestBed.inject<ApiErrorHandlingService>(
      ApiErrorHandlingService
    );
    exportFileService = TestBed.inject<ExportFileService>(ExportFileService);
  });

  test('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('submitPaymentRequest$', () => {
    // Happy path
    test('should return the submitPaymentRequestSuccess action that carries the request identifier', () => {
      // given
      const submitPaymentRequestAction = submitPaymentRequest();
      actions = hot('-a', { a: submitPaymentRequestAction });

      const submittedRequestIdentifier = '9977652';
      const serviceResponse = cold('-a|', {
        a: submittedRequestIdentifier,
      });
      requestPaymentService.submitRequest = jest.fn(() => serviceResponse);

      const submitPaymentRequestSuccessAction = submitPaymentRequestSuccess({
        submittedRequestIdentifier,
      });
      const expectedEffectResponse = cold('--b', {
        b: submitPaymentRequestSuccessAction,
      });

      // when then
      expect(effects.submitPaymentRequest$).toBeObservable(
        expectedEffectResponse
      );
    });

    // Error during service call
    test('should return errors action, with the ErrorSummary transformed by apiErrorHandlingService, on failure', () => {
      // given
      const errorSummary = new ErrorSummary([
        { errorMessage: 'test dummy error', componentId: '' },
      ]);
      const errorsAction = errors({ errorSummary });
      apiErrorHandlingService.transform = jest.fn(() => errorSummary);
      const apiErrorHandlingServiceSpy = jest.spyOn(
        apiErrorHandlingService,
        'transform'
      );
      const httpErrorResponse = new HttpErrorResponse({});

      actions = hot('-a', { a: submitPaymentRequest });

      const serviceResponse = cold('-#', {}, httpErrorResponse);
      requestPaymentService.submitRequest = jest.fn(() => serviceResponse);

      const expectedEffectResponse = cold('--b', { b: errorsAction });
      // when then
      expect(effects.submitPaymentRequest$).toBeObservable(
        expectedEffectResponse
      );
      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });

  xdescribe('downloadInvoiceFile$', () => {
    // Happy path
    test('should return the submitPaymentRequestSuccess action that carries the request identifier', () => {
      // given
      const downloadInvoiceAction = downloadInvoice();
      actions = hot('-a', { a: downloadInvoiceAction });

      const blob = new Blob([JSON.stringify('Invoice pdf content.', null, 2)], {
        type: 'application/pdf',
      });
      const httpResponse = new HttpResponse<Blob>({ body: blob });
      const requestPaymentServiceResponse = cold('-a|', {
        a: httpResponse,
      });
      requestPaymentService.downloadInvoice = jest.fn(
        () => requestPaymentServiceResponse
      );

      exportFileService.getContentDispositionFilename = jest.fn(
        () => 'attachment; filename="Results.xls'
      );

      // create a spy to verify the navigation will be called
      const requestPaymentServiceDownloadInvoiceSpy = jest.spyOn(
        requestPaymentService,
        'downloadInvoice'
      );
      const exportFileServiceGetContentDispositionFilenameSpy = jest.spyOn(
        exportFileService,
        'getContentDispositionFilename'
      );
      const exportFileServiceExportSpy = jest.spyOn(
        exportFileService,
        'export'
      );

      // when
      // subscribe to execute the Effect
      effects.downloadInvoiceFile$.subscribe();

      //then
      // verify the navigation has been called
      //expect(requestPaymentServiceDownloadInvoiceSpy).toHaveBeenCalled();
      //expect(exportFileServiceGetContentDispositionFilenameSpy).toHaveBeenCalledWith('attachment; filename="Results.xls');
      expect(exportFileServiceExportSpy).toHaveBeenCalled();
    });
  });
});
