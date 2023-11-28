import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import {
  AircraftOperatorContainerComponent,
  InstallationContainerComponent,
  InstallationTransferInputComponent,
  IsItAnInstallationTransferComponent,
  IsItAnInstallationTransferContainerComponent,
  OverviewAircraftOperatorComponent,
  OverviewComponent,
  OverviewContainerComponent,
  OverviewInstallationComponent,
  OverviewInstallationTransferComponent,
} from '@account-opening/operator/components';
import { RouterModule } from '@angular/router';
import { OPERATOR_ROUTES } from './operator.routes';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { FormatUkDatePipe } from '@shared/pipes';

@NgModule({
  declarations: [
    InstallationContainerComponent,
    AircraftOperatorContainerComponent,
    IsItAnInstallationTransferComponent,
    InstallationTransferInputComponent,
    IsItAnInstallationTransferContainerComponent,
    OverviewComponent,
    OverviewContainerComponent,
    OverviewInstallationComponent,
    OverviewAircraftOperatorComponent,
    OverviewInstallationTransferComponent,
  ],
  imports: [
    RouterModule.forChild(OPERATOR_ROUTES),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
    NgbTypeaheadModule,
  ],
  providers: [FormatUkDatePipe],
  exports: [
    OverviewInstallationTransferComponent,
    OverviewAircraftOperatorComponent,
    OverviewAircraftOperatorComponent,
    OverviewInstallationComponent,
  ],
})
export class OperatorModule {}
