import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  CancelRequestPaymentContainerComponent,
  PaymentRequestSubmittedContainerComponent,
  SetPaymentDetailsContainerComponent,
  CheckPaymentRequestContainerComponent,
} from '@request-payment/components';

export const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: SetPaymentDetailsContainerComponent,
    title: 'Payment',
  },
  {
    path: 'set-payment-details',
    canActivate: [LoginGuard],
    component: SetPaymentDetailsContainerComponent,
  },
  {
    path: 'check-payment-request',
    canActivate: [LoginGuard],
    //resolve: { goBackPath: CheckDocumentsRequestResolver },
    component: CheckPaymentRequestContainerComponent,
  },
  {
    path: 'cancel-request',
    canActivate: [LoginGuard],
    component: CancelRequestPaymentContainerComponent,
  },

  {
    path: 'request-submitted',
    canActivate: [LoginGuard],
    component: PaymentRequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RequestPaymentRoutingModule {}
