import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { SelectPublicationFileContainerComponent } from '@reports/report-publication/components/upload-publication-file/components/select-publication-file';
import { FileSubmittedComponent } from '@report-publication/components/upload-publication-file/components/file-submitted';
import { AddPublicationYearContainerComponent } from '@report-publication/components/upload-publication-file/components/add-publication-year/add-publication-year-container.component';
import { CheckAnswersAndSubmitContainerComponent } from '@report-publication/components/upload-publication-file/components/check-anwers-and-submit';
import { UploadPublicationFileWizardGuard } from '@report-publication/components/upload-publication-file/guards/upload-publication-file-wizard-guard';
import { CancelPublicationFileUploadRequestContainerComponent } from '@report-publication/components/upload-publication-file/components/cancel-request';
import { PublicationWizardsContainerComponent } from '@report-publication/components/publication-wizards-container/publication-wizards-container.component';

const routes: Routes = [
  {
    path: '',
    component: PublicationWizardsContainerComponent,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [UploadPublicationFileWizardGuard],
    children: [
      {
        path: '',
        component: SelectPublicationFileContainerComponent,
      },
      {
        path: 'add-publication-year',
        component: AddPublicationYearContainerComponent,
      },
      {
        path: 'check-answers-and-submit',
        component: CheckAnswersAndSubmitContainerComponent,
      },
      {
        path: 'cancel',
        component: CancelPublicationFileUploadRequestContainerComponent,
      },
    ],
  },
  {
    path: 'publication-file-submitted',
    component: FileSubmittedComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UploadPublicationFileRoutingModule {}
