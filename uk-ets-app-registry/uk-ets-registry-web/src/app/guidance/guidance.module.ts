import { NgModule } from '@angular/core';
import {
  ProposeTransactionComponent,
  FeaturesComponent,
  HelpSupportComponent,
  TasksComponent,
  UserRolesComponent,
  AccountOpeningComponent,
  AccessingRegistryComponent,
  AccountTypesComponent,
  ArComponent,
  ConfirmingYourRegistrySignInComponent,
  DocumentRequestsComponent,
  GuidanceSectionComponent,
  OtpAuthenticatorComponent,
} from '@guidance/components';
import { SharedModule } from '@shared/shared.module';
import { CommonModule } from '@angular/common';
import { GuidanceRoutingModule } from '@guidance/guidance-routing.module';
import { TransactionHoursComponent } from '@guidance/components/transaction-hours/transaction-hours.component';
import { UpdateTalComponent } from '@guidance/components/update-tal/update-tal.component';
import { IntroductionComponent } from '@guidance/components/introduction/introduction.component';
import { GuidanceModificationDateComponent } from '@guidance/components/guidance-modification-date/guidance-modification-date.component';
import { SurrenderObligationComponent } from '@guidance/components/surrender-obligation/surrender-obligation.component';

@NgModule({
  declarations: [
    GuidanceSectionComponent,
    AccessingRegistryComponent,
    UserRolesComponent,
    AccountOpeningComponent,
    ProposeTransactionComponent,
    TasksComponent,
    FeaturesComponent,
    HelpSupportComponent,
    AccountTypesComponent,
    ArComponent,
    ConfirmingYourRegistrySignInComponent,
    DocumentRequestsComponent,
    OtpAuthenticatorComponent,
    TransactionHoursComponent,
    UpdateTalComponent,
    IntroductionComponent,
    GuidanceModificationDateComponent,
    SurrenderObligationComponent,
  ],
  exports: [
    GuidanceSectionComponent,
    AccessingRegistryComponent,
    UserRolesComponent,
    AccountOpeningComponent,
    TasksComponent,
    FeaturesComponent,
    HelpSupportComponent,
    AccountTypesComponent,
    ArComponent,
    ConfirmingYourRegistrySignInComponent,
    DocumentRequestsComponent,
    OtpAuthenticatorComponent,
    TransactionHoursComponent,
    UpdateTalComponent,
    IntroductionComponent,
    GuidanceModificationDateComponent,
    SurrenderObligationComponent,
  ],
  imports: [GuidanceRoutingModule, SharedModule, CommonModule],
})
export class GuidanceModule {}
