import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmergencyOtpChangeRoutes } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';
import {
  ConfirmEmergencyOtpChangeContainerComponent,
  EmailEntryContainerComponent,
  EmailEntrySubmittedContainerComponent,
  EmergencyOtpChangeInitContainerComponent
} from '@user-management/emergency-otp-change/components';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: EmergencyOtpChangeRoutes.INIT,
        component: EmergencyOtpChangeInitContainerComponent
      },
      {
        path: EmergencyOtpChangeRoutes.EMAIL_ENTRY,
        component: EmailEntryContainerComponent
      },
      {
        path: EmergencyOtpChangeRoutes.EMAIL_SUBMITTED,
        component: EmailEntrySubmittedContainerComponent
      },
      {
        path: EmergencyOtpChangeRoutes.EMAIL_VERIFY,
        component: ConfirmEmergencyOtpChangeContainerComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmergencyOtpChangeRoutingModule {}
