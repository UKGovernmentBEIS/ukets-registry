import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmergencyPasswordOtpChangeRoutes } from '@user-management/emergency-password-otp-change/model';
import {
  ConfirmEmergencyPasswordOtpChangeContainerComponent,
  EmailEntryContainerComponent,
  EmailEntrySubmittedContainerComponent,
  EmergencyPasswordOtpChangeInitContainerComponent
} from '@user-management/emergency-password-otp-change/components';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: EmergencyPasswordOtpChangeRoutes.INIT,
        component: EmergencyPasswordOtpChangeInitContainerComponent
      },
      {
        path: EmergencyPasswordOtpChangeRoutes.EMAIL_ENTRY,
        component: EmailEntryContainerComponent
      },
      {
        path: EmergencyPasswordOtpChangeRoutes.EMAIL_SUBMITTED,
        component: EmailEntrySubmittedContainerComponent
      },
      {
        path: EmergencyPasswordOtpChangeRoutes.EMAIL_VERIFY,
        component: ConfirmEmergencyPasswordOtpChangeContainerComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmergencyPasswordOtpChangeRoutingModule {}
