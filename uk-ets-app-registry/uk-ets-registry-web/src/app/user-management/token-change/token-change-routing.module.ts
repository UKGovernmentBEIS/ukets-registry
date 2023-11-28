import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { NgModule } from '@angular/core';
import { TokenChangeRoutingPaths } from '@user-management/token-change/model/token-change-root-paths.enum';
import {
  TokenChangeClickedEmailContainerComponent,
  TokenChangeEnterReasonContainerComponent,
  TokenChangeEnterTokenContainerComponent,
  TokenChangeLinkExpiredContainerComponent,
  TokenChangeVerificationContainerComponent,
} from '@user-management/token-change/component';

export const routes: Routes = [
  {
    path: TokenChangeRoutingPaths.PAGE_1_ENTER_REASON,
    canActivate: [LoginGuard],
    component: TokenChangeEnterReasonContainerComponent,
  },
  {
    path: TokenChangeRoutingPaths.PAGE_2_ENTER_CODE,
    canActivate: [LoginGuard],
    component: TokenChangeEnterTokenContainerComponent,
  },
  {
    path:
      TokenChangeRoutingPaths.PAGE_3_VERIFY + '/:submittedRequestIdentifier',
    component: TokenChangeVerificationContainerComponent,
  },
  {
    path: TokenChangeRoutingPaths.PAGE_4_EXPIRED,
    component: TokenChangeLinkExpiredContainerComponent,
  },
  {
    path: TokenChangeRoutingPaths.PAGE_5_EMAIL_CLICKED,
    component: TokenChangeClickedEmailContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TokenChangeRoutingModule {}
