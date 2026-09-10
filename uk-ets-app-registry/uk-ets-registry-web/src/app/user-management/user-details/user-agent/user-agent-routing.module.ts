import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserAgentFormContainerComponent } from '@user-agent/components';
import { LoginGuard } from '@shared/guards/login.guard';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserAgentFormContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserAgentRoutingModule {}
