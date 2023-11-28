import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  PublicationOverviewContainerComponent,
  SectionDetailsContainerComponent,
  UnpublishFileComponent,
} from '@reports/report-publication/components';
import { PublicationWizardsContainerComponent } from '@reports/report-publication/components/publication-wizards-container/publication-wizards-container.component';
import { LoadSectionSetailsGuard } from './guards/load-section-details.guard';
import { UnpublishFileContainerComponent } from '@report-publication/components/unpublish-file/unpublish-file.-container.component';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: PublicationOverviewContainerComponent,
  },
  {
    path: 'section-details',
    canActivate: [LoginGuard, LoadSectionSetailsGuard],
    component: SectionDetailsContainerComponent,
  },
  {
    path: '',
    component: PublicationWizardsContainerComponent,
    canActivate: [LoginGuard],
    children: [
      {
        path: 'section-details/unpublish-file',
        component: UnpublishFileContainerComponent,
      },
    ],
  },
  {
    path: 'section-details/upload-publication-file',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import(
        './components/upload-publication-file/upload-publication-file.module'
      ).then((m) => m.UploadPublicationFileModule),
  },
  {
    path: 'section-details/update-publication-details',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import(
        './components/update-publication-details/update-publication-details.module'
      ).then((m) => m.UpdatePublicationDetailsModule),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReportPublicationRoutingModule {}
