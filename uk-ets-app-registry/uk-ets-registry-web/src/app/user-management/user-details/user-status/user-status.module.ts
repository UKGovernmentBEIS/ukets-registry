import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserStatusRoutingModule } from './user-status-routing.module';
import {
  SelectUserStatusActionContainerComponent,
  SelectUserStatusActionComponent,
  ConfirmUserStatusActionComponent,
  ConfirmUserStatusActionContainerComponent,
} from './components';
import * as fromUserStatus from './store/reducers';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { UserStatusEffects } from './store/effects';
import { UserDetailService } from '@user-management/service';
import { UserHeaderGuard } from '@user-management/guards/user-header.guard';
import { UserStatusPipe } from './pipes/user-status.pipe';
import { UserStatusContainerComponent } from './components/user-status-container/user-status-container.component';
import { UserDetailsModule } from '@user-management/user-details/user-details.module';

@NgModule({
  declarations: [
    SelectUserStatusActionContainerComponent,
    SelectUserStatusActionComponent,
    ConfirmUserStatusActionComponent,
    ConfirmUserStatusActionContainerComponent,
    UserStatusPipe,
    UserStatusContainerComponent,
  ],
  imports: [
    CommonModule,
    UserStatusRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromUserStatus.userStatusFeatureKey,
      fromUserStatus.reducer
    ),
    EffectsModule.forFeature([UserStatusEffects]),
    UserDetailsModule,
  ],
  providers: [UserDetailService, UserHeaderGuard],
  exports: [UserStatusPipe],
})
export class UserStatusModule {}
