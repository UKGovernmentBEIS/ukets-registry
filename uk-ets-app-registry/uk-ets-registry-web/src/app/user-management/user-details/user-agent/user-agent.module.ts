import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserAgentRoutingModule } from '@user-agent/user-agent-routing.module';
import { EffectsModule } from '@ngrx/effects';
import * as fromUserAgent from '@user-agent/store/reducers';
import { StoreModule } from '@ngrx/store';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import {
  UserAgentEffects,
  UserAgentNavigationEffects,
} from '@user-agent/store';
import {
  UserAgentFormComponent,
  UserAgentFormContainerComponent,
} from '@user-agent/components';
import { UserDetailsModule } from '@user-management/user-details/user-details.module';

@NgModule({
  declarations: [UserAgentFormContainerComponent, UserAgentFormComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    UserDetailsModule,
    UserAgentRoutingModule,
    StoreModule.forFeature(
      fromUserAgent.userAgentFeatureKey,
      fromUserAgent.reducer
    ),
    EffectsModule.forFeature([UserAgentEffects, UserAgentNavigationEffects]),
  ],
})
export class UserAgentModule {}
