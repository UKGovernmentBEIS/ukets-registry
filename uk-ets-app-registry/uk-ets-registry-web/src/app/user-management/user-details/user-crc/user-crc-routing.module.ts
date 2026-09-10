import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { UserCrcFormContainerComponent } from '@user-crc/components';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserCrcFormContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserCrcRoutingModule {}
