import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RequestPaymentRoutingModule } from '@request-payment/request-payment-routing.module';
import { SharedModule } from '@shared/shared.module';
import {
  SetPaymentDetailsContainerComponent,
  PaymentRequestSubmittedContainerComponent,
  CancelRequestPaymentContainerComponent,
  SetPaymentDetailsComponent,
  CheckPaymentRequestComponent,
  CheckPaymentRequestContainerComponent,
} from '@request-payment/components';
import { StoreModule } from '@ngrx/store';
import {
  requestPaymentFeatureKey,
  requestPaymentReducer,
} from '@request-payment/store/reducers';
import { EffectsModule } from '@ngrx/effects';
import {
  RequestPaymentEffects,
  RequestPaymentNavigationEffects,
} from '@request-payment/store/effects';
import { ReactiveFormsModule } from '@angular/forms';
import { PaymentRequestSubmittedComponent } from './components/payment-request-submitted/payment-request-submitted.component';

@NgModule({
  declarations: [
    SetPaymentDetailsContainerComponent,
    SetPaymentDetailsComponent,
    PaymentRequestSubmittedContainerComponent,
    CancelRequestPaymentContainerComponent,
    CheckPaymentRequestComponent,
    CheckPaymentRequestContainerComponent,
    PaymentRequestSubmittedComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RequestPaymentRoutingModule,
    StoreModule.forFeature(requestPaymentFeatureKey, requestPaymentReducer),
    EffectsModule.forFeature([
      RequestPaymentEffects,
      RequestPaymentNavigationEffects,
    ]),
    ReactiveFormsModule,
  ],
})
export class RequestPaymentModule {}
