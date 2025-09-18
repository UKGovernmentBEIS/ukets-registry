import { TestBed } from '@angular/core/testing';

import { RequestPaymentService } from './request-payment.service';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

describe('RequestPaymentService', () => {
  let service: RequestPaymentService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        // ... other test providers
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });
    service = TestBed.inject(RequestPaymentService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  test('should be created', () => {
    expect(service).toBeTruthy();
  });

  test('should HTTP POST to /payment-request.submit url to submit a payment request', () => {
    //given
    let response = '1234567';
    const request = {
      recipientUrid: 'UK9393',
      amount: 12903,
      description: 'A test payment request',
      parentRequestId: 'UK983983983',
    };
    // when
    service.submitRequest(request).subscribe((result) => (response = result));

    // then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/payment-request.submit`)
    );

    expect(req.request.url).toBe(`${baseApiUrl}/payment-request.submit`);
    expect(req.request.method).toEqual('POST');
  });

  test('should HTTP GET to /payment-request.invoice-preview url to dowamload a payment request invoice', () => {
    //given
    const blob = new Blob([JSON.stringify('Invoice pdf content.', null, 2)], {
      type: 'application/pdf',
    });
    let httpResponse = new HttpResponse<Blob>({ body: blob });
    const request = {
      recipientUrid: 'UK9393',
      amount: 12903,
      description: 'A test payment request',
      parentRequestId: 'UK983983983',
    };
    // when
    service
      .downloadInvoice(request)
      .subscribe((result) => (httpResponse = result));

    // then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/payment-request.invoice-preview`)
    );

    expect(req.request.url).toBe(
      `${baseApiUrl}/payment-request.invoice-preview`
    );
    expect(req.request.params.keys()).toContain('parentRequestId');
    expect(req.request.params.get('parentRequestId')).toEqual(
      request.parentRequestId
    );
    expect(req.request.params.keys()).toContain('amount');
    expect(req.request.params.get('amount')).toEqual(request.amount.toString());
    expect(req.request.params.keys()).toContain('description');
    expect(req.request.params.get('description')).toEqual(request.description);
    expect(req.request.method).toEqual('GET');
  });

  afterEach(() => httpMock.verify());
});
