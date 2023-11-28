import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { KpAdministrationRoutingModule } from './kp-administration-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import {
  MessageDetailsContainerComponent,
  MessageFormComponent,
  MessageFormContainerComponent,
  MessageHeaderComponent,
  MessageListContainerComponent,
  SearchMessagesFormComponent,
  SearchMessagesResultsComponent,
  SendMessageConfirmationComponent,
} from '@kp-administration/itl-messages/components';
import {
  itlFeatureKey,
  ItlMessageDetailsEffect,
  ItlMessagesEffect,
  ItlMessageSendEffects,
  itlMessagesReducers,
} from '@kp-administration/store';
import { MessageApiService } from '@kp-administration/itl-messages/service';
import { MessageHeaderGuard } from '@kp-administration/itl-messages/guards';
import {
  NoticeDetailsComponent,
  NoticeDetailsContainerComponent,
  NoticeDetailsHeaderComponent,
  NoticeDetailsListComponent,
  NoticeListComponent,
  NoticeListContainerComponent,
} from '@kp-administration/itl-notices/components';
import { NoticeApiService } from '@kp-administration/itl-notices/service';
import { ItlNoticesEffects } from '@kp-administration/store/effects/itl-notices.effects';
import { NoticeDetailsGuard } from '@kp-administration/itl-notices/guards';
import { ReconciliationAdministrationContainerComponent } from '@kp-administration/reconciliation';
import { ItlReconciliationEffects } from '@kp-administration/store/effects/itl-reconciliation-effects';

@NgModule({
  declarations: [
    MessageListContainerComponent,
    SearchMessagesFormComponent,
    SearchMessagesResultsComponent,
    MessageFormComponent,
    MessageFormContainerComponent,
    SendMessageConfirmationComponent,
    MessageDetailsContainerComponent,
    MessageHeaderComponent,
    NoticeListComponent,
    NoticeListContainerComponent,
    NoticeDetailsContainerComponent,
    NoticeDetailsComponent,
    NoticeDetailsListComponent,
    NoticeDetailsHeaderComponent,
    ReconciliationAdministrationContainerComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    StoreModule.forFeature(itlFeatureKey, itlMessagesReducers),
    EffectsModule.forFeature([
      ItlMessagesEffect,
      ItlMessageSendEffects,
      ItlMessageDetailsEffect,
      ItlNoticesEffects,
      ItlReconciliationEffects,
    ]),
    KpAdministrationRoutingModule,
  ],
  providers: [
    MessageApiService,
    MessageHeaderGuard,
    NoticeApiService,
    NoticeDetailsGuard,
  ],
})
export class KpAdministrationModule {}
