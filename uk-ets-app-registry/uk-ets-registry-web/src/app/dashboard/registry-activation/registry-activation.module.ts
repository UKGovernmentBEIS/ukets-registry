import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnrolledComponent } from './components/enrolled';
import { RegistryActivationComponent } from './components/registry-activation';
import {
  ActivationCodeRequestComponent,
  ActivationCodeRequestContainerComponent
} from './components/activation-code-request';
import { RouterModule } from '@angular/router';
import { REGISTRY_ACTIVATION_ROUTES } from './registry-activation.routes';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import {
  ActivationCodeRequestSubmittedComponent,
  ActivationCodeRequestSubmittedContainerComponent
} from './components/activation-code-request-submitted';
import * as fromRegistryActivation from './reducers/registry-activation-reducer';

@NgModule({
  declarations: [
    RegistryActivationComponent,
    EnrolledComponent,
    ActivationCodeRequestContainerComponent,
    ActivationCodeRequestComponent,
    ActivationCodeRequestSubmittedContainerComponent,
    ActivationCodeRequestSubmittedComponent
  ],
  imports: [
    RouterModule.forChild(REGISTRY_ACTIVATION_ROUTES),
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromRegistryActivation.registryActivationFeatureKey,
      fromRegistryActivation.reducer
    )
  ]
})
export class RegistryActivationModule {}
