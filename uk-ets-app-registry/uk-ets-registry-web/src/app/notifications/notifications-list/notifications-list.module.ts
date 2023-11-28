import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { NotificationsResultsRoutingModule } from '@notifications/notifications-list/notifications-results-routing.module';
import {
  NotificationsResultsComponent,
  NotificationsResultsContainerComponent,
  SearchNotificationsFormComponentComponent,
} from '@notifications/notifications-list/components/notifications-results';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import * as fromNotificationsList from '@notifications/notifications-list/reducers';
import { NotificationsListEffect } from '@notifications/notifications-list/effects';
import { NotificationsResultsService } from '@notifications/notifications-list/service';
import { SortService } from '@shared/search/sort/sort.service';
import { ClearNotificationsDetailsGuard } from '@notifications/notifications-details/guards';

@NgModule({
  declarations: [
    NotificationsResultsContainerComponent,
    NotificationsResultsComponent,
    SearchNotificationsFormComponentComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    NotificationsResultsRoutingModule,
    EffectsModule.forFeature([NotificationsListEffect]),
    StoreModule.forFeature(
      fromNotificationsList.notificationsListFeatureKey,
      fromNotificationsList.reducer
    ),
  ],
  providers: [
    NotificationsResultsService,
    SortService,
    ClearNotificationsDetailsGuard,
  ],
})
export class NotificationsListModule {}
