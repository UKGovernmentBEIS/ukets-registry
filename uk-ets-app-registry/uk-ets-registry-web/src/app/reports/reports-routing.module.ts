import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import {
  DownloadReportsListContainerComponent,
  StandardReportsContainerComponent,
} from '@reports/components';
import { ClearReportPublicationGuard } from '@report-publication/guards';

const routes: Routes = [
  // {
  //   path: '',
  //   canActivate: [LoginGuard],
  // },
  {
    path: 'downloads',
    component: DownloadReportsListContainerComponent,
    title: 'Reports',
  },
  {
    path: 'standard',
    component: StandardReportsContainerComponent,
  },
  {
    path: 'ets-report-publication',
    canDeactivate: [ClearReportPublicationGuard],
    loadChildren: () =>
      import('./report-publication/report-publication.module').then(
        (m) => m.ReportPublicationModule
      ),
  },
  {
    path: 'kp-report-publication',
    canDeactivate: [ClearReportPublicationGuard],
    loadChildren: () =>
      import('./report-publication/report-publication.module').then(
        (m) => m.ReportPublicationModule
      ),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReportsRoutingModule {}
