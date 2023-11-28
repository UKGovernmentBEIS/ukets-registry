import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@registry-web/shared/guards';
import { ConfirmDeleteFileContainerComponent } from '@delete-file/wizard/components/confirm-delete-file/confirm-delete-file-container.component';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: ConfirmDeleteFileContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DeleteFileRoutingModule {}
