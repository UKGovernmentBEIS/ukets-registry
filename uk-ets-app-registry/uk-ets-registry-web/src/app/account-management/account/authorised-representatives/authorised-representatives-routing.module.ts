import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  AddRepresentativeContainerComponent,
  CancelUpdateRequestContainerComponent,
  CheckUpdateRequestContainerComponent,
  ReplaceRepresentativeContainerComponent,
  RequestSubmittedContainerComponent,
  SelectAccessRightsContainerComponent,
  SelectRepresentativeTableContainerComponent,
  SelectTypeContainerComponent,
} from '@authorised-representatives/components';
import {
  CheckUpdateRequestResolver,
  SelectAccessRightsResolver,
  UpdateTypesResolver,
} from '@authorised-representatives/resolvers';
import { AuthorisedRepresentativesRoutePaths } from '@authorised-representatives/model/authorised-representatives-route-paths.model';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    resolve: { updateTypes: UpdateTypesResolver },
    component: SelectTypeContainerComponent,
  },
  {
    path: AuthorisedRepresentativesRoutePaths.SELECT_UPDATE_TYPE,
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: AuthorisedRepresentativesRoutePaths.ADD_REPRESENTATIVE,
    canLoad: [LoginGuard],
    component: AddRepresentativeContainerComponent,
  },
  {
    path: AuthorisedRepresentativesRoutePaths.REPLACE_REPRESENTATIVE,
    canLoad: [LoginGuard],
    component: ReplaceRepresentativeContainerComponent,
  },
  {
    path: AuthorisedRepresentativesRoutePaths.SELECT_ACCESS_RIGHTS,
    canLoad: [LoginGuard],
    resolve: { goBackPath: SelectAccessRightsResolver },
    component: SelectAccessRightsContainerComponent,
  },
  {
    path: AuthorisedRepresentativesRoutePaths.SELECT_AR_TABLE,
    canLoad: [LoginGuard],
    component: SelectRepresentativeTableContainerComponent,
  },
  {
    path: AuthorisedRepresentativesRoutePaths.CHECK_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    resolve: { goBackPath: CheckUpdateRequestResolver },
    component: CheckUpdateRequestContainerComponent,
  },
  {
    path: AuthorisedRepresentativesRoutePaths.REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
  {
    path: AuthorisedRepresentativesRoutePaths.CANCEL_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: CancelUpdateRequestContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthorisedRepresentativesRoutingModule {}
