import { NgModule } from '@angular/core';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { AccountModule } from '@account-management/account/account.module';
import { CommonModule } from '@angular/common';
import { OperatorUpdateWizardRoutingModule } from './operator-update-wizard-routing.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import {
  OperatorUpdateComponent,
  OperatorUpdateContainerComponent,
} from '@operator-update/components/operator-update';
import { InstallationUpdateComponent } from '@operator-update/components/installation-update';
import { OperatorUpdateEffects } from '@operator-update/effects';
import { OperatorUpdateApiService } from '@operator-update/services';
import * as fromOperatorUpdate from './reducers';
import {
  CheckUpdateRequestComponent,
  CheckUpdateRequestContainerComponent,
} from '@operator-update/components/check-update-request';
import { CancelUpdateRequestContainerComponent } from '@operator-update/components/cancel-update-request';
import { RequestSubmittedContainerComponent } from '@operator-update/components/request-submitted';
import { AircraftUpdateComponent } from '@operator-update/components/aircraft-update';
import { MaritimeUpdateComponent } from './components/maritime-update';

@NgModule({
  declarations: [
    OperatorUpdateContainerComponent,
    OperatorUpdateComponent,
    InstallationUpdateComponent,
    CancelUpdateRequestContainerComponent,
    CheckUpdateRequestContainerComponent,
    CheckUpdateRequestComponent,
    RequestSubmittedContainerComponent,
    AircraftUpdateComponent,
    MaritimeUpdateComponent
  ],
  imports: [
    CommonModule,
    OperatorUpdateWizardRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    AccountModule,
    StoreModule.forFeature(
      fromOperatorUpdate.operatorUpdateFeatureKey,
      fromOperatorUpdate.reducer
    ),
    EffectsModule.forFeature([OperatorUpdateEffects]),
  ],
  providers: [OperatorUpdateApiService],
})
export class OperatorUpdateWizardModule {}
