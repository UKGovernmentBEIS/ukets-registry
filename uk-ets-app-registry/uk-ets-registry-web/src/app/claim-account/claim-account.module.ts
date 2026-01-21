import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { LoginGuard } from '@registry-web/shared/guards';
import {
  ClaimAccountFormContainerComponent,
  ClaimAccountSubmittedComponent,
} from '@claim-account/components';
import { ClaimAccountEffects, claimAccountFeature } from '@claim-account/store';
import { CLAIM_ACCOUNT_REQUEST_SUBMITTED } from '@claim-account/claim-account.const';
import { canActivateClaimAccountRequestSubmitted } from '@claim-account/claim-account.guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: ClaimAccountFormContainerComponent,
  },
  {
    path: CLAIM_ACCOUNT_REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard, canActivateClaimAccountRequestSubmitted],
    component: ClaimAccountSubmittedComponent,
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
    StoreModule.forFeature(claimAccountFeature),
    EffectsModule.forFeature([ClaimAccountEffects]),
  ],
})
export class ClaimAccountModule {}
