import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { LoginGuard } from '@registry-web/shared/guards';
import { SharedModule } from '@shared/shared.module';
import {
  RegulatorNoticeDetailsEffects,
  regulatorNoticeDetailsFeature,
} from '@regulator-notice-management/details/store';
import {
  RegulatorNoticeDetailsContainerComponent,
  RegulatorNoticeDetailsNotesContainerComponent,
  RegulatorNoticeDetailsPageContainerComponent,
} from '@regulator-notice-management/details/components';
import { RegulatorNoticeDetailsHistoryContainerComponent } from '@regulator-notice-management/details/components/regulator-notice-details-history';
import {
  NOTES_LIST_PATH,
  TASK_NOTES_PARENT_DETAILS_PATH_TOKEN,
  TaskNotesEffects,
  taskNotesFeature,
} from '@registry-web/notes/task-notes';
import { REGULATOR_NOTICE_DETAILS_PATH } from '@regulator-notice-management/details/regulator-notice-details.const';

export const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: RegulatorNoticeDetailsPageContainerComponent,
    title: 'Notice details',
    children: [
      {
        path: '',
        component: RegulatorNoticeDetailsContainerComponent,
      },
      {
        path: NOTES_LIST_PATH,
        component: RegulatorNoticeDetailsNotesContainerComponent,
      },
      {
        path: 'history',
        component: RegulatorNoticeDetailsHistoryContainerComponent,
      },
    ],
  },
  {
    path: 'notes',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import(
        '@registry-web/notes/task-notes/task-notes-wizard/task-notes-wizard-routing.module'
      ).then((m) => m.TaskNotesRoutingModule),
  },
];

@NgModule({
  declarations: [],
  imports: [
    RouterModule.forChild(routes),
    SharedModule,
    StoreModule.forFeature(regulatorNoticeDetailsFeature),
    EffectsModule.forFeature([RegulatorNoticeDetailsEffects]),
    StoreModule.forFeature(taskNotesFeature),
    EffectsModule.forFeature([TaskNotesEffects]),
  ],
  providers: [
    {
      provide: TASK_NOTES_PARENT_DETAILS_PATH_TOKEN,
      useValue: REGULATOR_NOTICE_DETAILS_PATH,
    },
  ],
})
export class RegulatorNoticeDetailsModule {}
