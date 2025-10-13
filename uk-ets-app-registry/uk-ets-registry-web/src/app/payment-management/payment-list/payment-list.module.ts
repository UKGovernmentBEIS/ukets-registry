import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentListRoutingModule } from '@payment-management/payment-list/payment-list-routing.module';
import {
  PaymentListContainerComponent,
  SearchPaymentsFormComponent,
  SearchPaymentsResultsComponent,
} from '@payment-management/payment-list/components';
import * as fromPaymentList from '@payment-management/payment-list/store/reducer/payment-list.reducer';
import {
  PaymentListEffects,
  PaymentListNavigationEffects,
} from '@payment-management/payment-list/store/effects';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SortService } from '@shared/search/sort/sort.service';
import { PaymentListResolver } from '@payment-management/resolvers';

@NgModule({
  declarations: [
    PaymentListContainerComponent,
    SearchPaymentsFormComponent,
    SearchPaymentsResultsComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    PaymentListRoutingModule,
    StoreModule.forFeature(
      fromPaymentList.paymentListFeatureKey,
      fromPaymentList.reducer
    ),
    EffectsModule.forFeature([
      PaymentListEffects,
      PaymentListNavigationEffects,
    ]),
  ],
  providers: [SortService, PaymentListResolver],
})
export class PaymentListModule {}
