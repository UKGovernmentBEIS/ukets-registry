import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserCrcRoutingModule } from './user-crc-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { UserDetailsModule } from '@user-management/user-details/user-details.module';
import {
  UserCrcFormContainerComponent,
  UserCrcFormComponent,
} from '@user-crc/components';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import * as fromUserCrc from '@user-crc/store/reducers';
import { UserCrcEffects, UserCrcNavigationEffects } from '@user-crc/store';

@NgModule({
  declarations: [UserCrcFormComponent, UserCrcFormContainerComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    UserDetailsModule,
    UserCrcRoutingModule,
    StoreModule.forFeature(fromUserCrc.userCrcFeatureKey, fromUserCrc.reducer),
    EffectsModule.forFeature([UserCrcEffects, UserCrcNavigationEffects]),
  ],
})
export class UserCrcModule {}
