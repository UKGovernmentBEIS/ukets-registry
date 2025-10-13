import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable, of } from 'rxjs';
import {
  PaymentMethod,
  PaymentRequestDetails,
  PaymentStatus,
} from '@request-payment/model';
import { PaymentCompleteResponse } from '@task-management/model';

@Injectable({
  providedIn: 'root',
})
export class RequestPaymentService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  submitRequest(request: PaymentRequestDetails): Observable<string> {
    let params = new HttpParams();
    params = params.set('parentRequestId', request.parentRequestId);
    return this.httpClient.post<string>(
      `${this.ukEtsRegistryApiBaseUrl}/payment-request.submit`,
      request,
      { params }
    );
  }

  downloadInvoice(
    request: PaymentRequestDetails
  ): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.append('parentRequestId', request.parentRequestId);
    params = params.append('amount', request.amount);
    params = params.append('description', request.description);
    return this.httpClient.get(
      `${this.ukEtsRegistryApiBaseUrl}/payment-request.invoice-preview`,
      {
        headers,
        observe: 'response',
        responseType: 'blob',
        params,
      }
    );
  }

  downloadReceipt(uuid: string): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.append('paymentUUID', uuid);
    return this.httpClient.get(
      `${this.ukEtsRegistryApiBaseUrl}/payment-request.receipt`,
      {
        headers,
        observe: 'response',
        responseType: 'blob',
        params,
      }
    );
  }

  submitMakePayment(makePaymentRequest: {
    uuid: string;
    method: PaymentMethod;
  }): Observable<string> {
    let params = new HttpParams();
    params = params.set('paymentUUID', makePaymentRequest.uuid);
    params = params.set('paymentMethod', makePaymentRequest.method);
    return this.httpClient.post(
      `${this.ukEtsRegistryApiBaseUrl}/make.payment.submit`,
      makePaymentRequest,
      {
        params,
        responseType: 'text',
      }
    );
  }

  bacsPaymentCompleteOrCancel(bacsPaymentRequest: {
    uuid: string;
    status: PaymentStatus;
  }): Observable<boolean> {
    let params = new HttpParams();
    params = params.set('paymentUUID', bacsPaymentRequest.uuid);
    params = params.set('paymentStatus', bacsPaymentRequest.status);
    return this.httpClient.post<boolean>(
      `${this.ukEtsRegistryApiBaseUrl}/bacs.payment.complete.or.cancel`,
      bacsPaymentRequest,
      {
        params,
      }
    );
  }

  getMakePaymentResponse(
    requestId: string
  ): Observable<PaymentCompleteResponse> {
    let params = new HttpParams();
    params = params.set('referenceNumber', requestId);
    return this.httpClient.get<PaymentCompleteResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/make.payment.get.response`,
      {
        params,
      }
    );
  }

  getMakePaymentWebLinkResponse(
    paymentUUID: string
  ): Observable<PaymentCompleteResponse> {
    let params = new HttpParams();
    params = params.set('paymentUUID', paymentUUID);
    return this.httpClient.get<PaymentCompleteResponse>(
      `${this.ukEtsRegistryApiBaseUrl.replace('/api-registry', '')}/payment/${paymentUUID}/completed`
      // {
      //   params,
      // }
    );
  }
}
