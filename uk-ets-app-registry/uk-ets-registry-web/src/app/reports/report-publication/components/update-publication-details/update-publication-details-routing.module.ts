import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { UpdateSectionDetailsContainerComponent } from '@reports/report-publication/components/update-publication-details/components/update-section-details/update-section-details-container.component';
import { UpdateSchedulerDetailsContainerComponent } from '@report-publication/components/update-publication-details/components/update-scheduler-details';
import { CheckYourAnswersContainerComponent } from '@report-publication/components/update-publication-details/components/check-your-answers/check-your-answers-container.component';
import { UpdateSubmittedComponent } from '@report-publication/components/update-publication-details/components/update-submitted';
import { ClearPublicationDetailsWizardGuard } from '@report-publication/components/update-publication-details/guards/clear-publication-details-wizard-guard';
import { CancelUpdateDetailsRequestContainerComponent } from '@report-publication/components/update-publication-details/components/cancel-request';
import { PublicationWizardsContainerComponent } from '@report-publication/components/publication-wizards-container/publication-wizards-container.component';

const routes: Routes = [
  {
    path: '',
    component: PublicationWizardsContainerComponent,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [ClearPublicationDetailsWizardGuard],
    children: [
      {
        path: '',
        component: UpdateSectionDetailsContainerComponent,
      },
      {
        path: 'update-scheduler-details',
        component: UpdateSchedulerDetailsContainerComponent,
      },
      {
        path: 'check-and-submit',
        component: CheckYourAnswersContainerComponent,
      },
      {
        path: 'cancel',
        component: CancelUpdateDetailsRequestContainerComponent,
      },
    ],
  },
  {
    path: 'publication-update-submitted',
    component: UpdateSubmittedComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UpdatePublicationDetailsRoutingModule {}
