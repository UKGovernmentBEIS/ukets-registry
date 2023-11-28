import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SecondApprovalNecessaryComponent } from './second-approval-necessary/second-approval-necessary.component';
import { TransfersOutsideListComponent } from './transfers-outside-list/transfers-outside-list.component';
import { TrustedAccountListRoutingModule } from './trusted-account-list-routing.module';
import { OverviewComponent } from './overview/overview.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { SinglePersonSurrenderExcessAllocationComponent } from '@account-opening/trusted-account-list/single-person-surrender-excess-allocation/single-person-surrender-excess-allocation.component';
import { EffectsModule } from '@ngrx/effects';
import { TransactionRulesNavigationEffects } from '@account-opening/trusted-account-list/trusted-account-list-navigation.effects';

@NgModule({
  declarations: [
    SecondApprovalNecessaryComponent,
    TransfersOutsideListComponent,
    SinglePersonSurrenderExcessAllocationComponent,
    OverviewComponent,
  ],
  imports: [
    EffectsModule.forFeature([TransactionRulesNavigationEffects]),
    TrustedAccountListRoutingModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
  ],
})
export class TrustedAccountListModule {}
