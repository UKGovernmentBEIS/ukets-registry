import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { UserDetailsUpdateContainerComponent } from '@user-update/component/user-details-update';
import { UserDetailsModule } from '@user-management/user-details/user-details.module';
import { StoreModule } from '@ngrx/store';
import * as fromUserDetailsUpdate from '@user-update/reducers';
import { UserDetailsUpdateWizardRoutingModule } from '@user-update/user-details-update-wizard-routing.module';
import { EffectsModule } from '@ngrx/effects';
import { UserDetailsUpdateEffects } from '@user-update/effects';
import { UserHeaderGuard } from '@user-management/guards';
import { UserDetailsUpdateApiService } from '@user-update/services';
import {
  SelectTypeUserDetailsUpdateComponent,
  SelectTypeUserDetailsUpdateContainerComponent,
} from '@user-update/component/select-type-user-details-update';
import { UpdateUserPersonalDetailsContainerComponent } from '@user-update/component/update-user-personal-details';
import { CancelUpdateRequestContainerComponent } from '@user-update/component/cancel-update-request';
import { UpdateUserWorkDetailsContainerComponent } from '@user-update/component/update-user-work-details';
import {
  CheckUserDetailsUpdateRequestComponent,
  CheckUserDetailsUpdateRequestContainerComponent,
} from '@user-update/component/check-user-details-update-request';
import { UserDetailService } from '@user-management/service';
import { UserDetailsUpdatePipe } from '@user-update/pipes';
import {
  RequestSubmittedComponent,
  RequestSubmittedForMinorChangesComponent,
  RequestSubmittedContainerComponent,
} from '@user-update/component/request-submitted';
import {
  UserDeactivationCommentComponent,
  UserDeactivationCommentContainerComponent,
} from '@user-update/component/user-deactivation-comment';
import {
  CheckDeactivationDetailsComponent,
  CheckDeactivationDetailsContainerComponent,
} from '@user-update/component/check-deactivation-details';
import { UpdateUserMemorablePhraseContainerComponent } from '@user-update/component/update-user-memorable-phrase';

@NgModule({
  declarations: [
    UserDetailsUpdateContainerComponent,
    SelectTypeUserDetailsUpdateContainerComponent,
    SelectTypeUserDetailsUpdateComponent,
    UpdateUserPersonalDetailsContainerComponent,
    CancelUpdateRequestContainerComponent,
    UpdateUserWorkDetailsContainerComponent,
    CheckUserDetailsUpdateRequestContainerComponent,
    CheckUserDetailsUpdateRequestComponent,
    UserDetailsUpdatePipe,
    RequestSubmittedComponent,
    RequestSubmittedForMinorChangesComponent,
    RequestSubmittedContainerComponent,
    UserDeactivationCommentContainerComponent,
    UserDeactivationCommentComponent,
    CheckDeactivationDetailsContainerComponent,
    CheckDeactivationDetailsComponent,
    UpdateUserMemorablePhraseContainerComponent,
  ],
  imports: [
    CommonModule,
    UserDetailsUpdateWizardRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromUserDetailsUpdate.userDetailsUpdateFeatureKey,
      fromUserDetailsUpdate.reducer
    ),
    EffectsModule.forFeature([UserDetailsUpdateEffects]),
    UserDetailsModule,
  ],
  providers: [UserHeaderGuard, UserDetailsUpdateApiService, UserDetailService],
})
export class UserDetailsUpdateWizardModule {}
