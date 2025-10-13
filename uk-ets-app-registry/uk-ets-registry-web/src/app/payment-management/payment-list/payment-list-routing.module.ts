import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentListContainerComponent } from '@payment-management/payment-list/components';
import { LoginGuard } from '@shared/guards';
import { PaymentListResolver } from '@payment-management/resolvers';
import { createPaymentListErrorMap } from '@payment-management/model';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: PaymentListContainerComponent,
    resolve: {
      search: PaymentListResolver,
    },
    data: {
      errorMap: createPaymentListErrorMap(),
    },
    title: 'Payments',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PaymentListRoutingModule {}
