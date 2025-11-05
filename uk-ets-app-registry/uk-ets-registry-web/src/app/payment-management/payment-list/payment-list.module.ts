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
import * as fromTaskDetails from '@registry-web/task-management/task-details/reducers/task-details.reducer';
import {
  TaskDetailsEffects,
  TaskDetailsNavigationEffects,
} from '@task-details/effects';

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
    StoreModule.forFeature(
      fromTaskDetails.taskDetailsFeatureKey,
      fromTaskDetails.reducer
    ),
    EffectsModule.forFeature([
      TaskDetailsEffects,
      TaskDetailsNavigationEffects,
    ]),
  ],
  providers: [SortService, PaymentListResolver],
})
export class PaymentListModule {}
