import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AddRepresentativeComponent,
  AddRepresentativeContainerComponent,
  CancelUpdateRequestContainerComponent,
  CheckUpdateRequestComponent,
  CheckUpdateRequestContainerComponent,
  ReplaceRepresentativeComponent,
  ReplaceRepresentativeContainerComponent,
  RequestSubmittedContainerComponent,
  SelectAccessRightsComponent,
  SelectAccessRightsContainerComponent,
  SelectRepresentativeTableComponent,
  SelectRepresentativeTableContainerComponent,
  SelectTypeComponent,
  SelectTypeContainerComponent,
} from '@authorised-representatives/components';
import { AccountModule } from '@account-management/account/account.module';
import { SharedModule } from '@shared/shared.module';
import { AuthorisedRepresentativesRoutingModule } from './authorised-representatives-routing.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import * as fromAuthorisedRepresentatives from './reducers';
import {
  AuthorisedRepresentativesEffects,
  AuthorisedRepresentativesNavigationEffects,
} from '@authorised-representatives/effects';

import { ReactiveFormsModule } from '@angular/forms';
import {
  CheckUpdateRequestResolver,
  SelectAccessRightsResolver,
  UpdateTypesResolver,
} from '@authorised-representatives/resolvers';
import { AuthorisedRepresentativesApiService } from '@authorised-representatives/services';

@NgModule({
  declarations: [
    SelectTypeContainerComponent,
    SelectTypeComponent,
    AddRepresentativeComponent,
    CancelUpdateRequestContainerComponent,
    CancelUpdateRequestContainerComponent,
    SelectRepresentativeTableContainerComponent,
    SelectRepresentativeTableComponent,
    CheckUpdateRequestContainerComponent,
    CheckUpdateRequestComponent,
    RequestSubmittedContainerComponent,
    SelectAccessRightsComponent,
    SelectAccessRightsContainerComponent,
    AddRepresentativeContainerComponent,
    ReplaceRepresentativeComponent,
    ReplaceRepresentativeContainerComponent,
  ],
  imports: [
    CommonModule,
    AuthorisedRepresentativesRoutingModule,
    StoreModule.forFeature(
      fromAuthorisedRepresentatives.authorisedRepresentativesFeatureKey,
      fromAuthorisedRepresentatives.reducer
    ),
    EffectsModule.forFeature([
      AuthorisedRepresentativesEffects,
      AuthorisedRepresentativesNavigationEffects,
    ]),
    AccountModule,
    ReactiveFormsModule,
    SharedModule,
  ],
  providers: [
    UpdateTypesResolver,
    SelectAccessRightsResolver,
    CheckUpdateRequestResolver,
    AuthorisedRepresentativesApiService,
  ],
})
export class AuthorisedRepresentativesModule {}
