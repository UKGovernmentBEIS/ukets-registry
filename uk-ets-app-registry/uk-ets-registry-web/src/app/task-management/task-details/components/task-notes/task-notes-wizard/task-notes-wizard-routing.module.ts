import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { AddNoteFormContainerComponent } from './components/add-note-form-container.component';
import { CheckAndConfirmAddNoteContainerComponent } from './components/check-and-confirm-add-note-container.component';
import { AddNoteSuccessContainerComponent } from './components/add-note-success-container.component';
import { CancelAddNoteContainerComponent } from './components/cancel-add-note-container.component';
import { DeleteNoteContainerComponent } from './components/delete-note-container.component';
import { DeleteNoteSuccessContainerComponent } from './components/delete-note-success-container.component';
import { NotesWizardPathsModel } from '@registry-web/notes/model/notes-wizard-paths.model';

const routes: Routes = [
  {
    path: NotesWizardPathsModel.ADD_NOTE,
    component: AddNoteFormContainerComponent,
  },
  {
    path: NotesWizardPathsModel.CHECK_AND_CONFIRM_ADD_NOTE,
    component: CheckAndConfirmAddNoteContainerComponent,
  },
  {
    path: NotesWizardPathsModel.ADD_NOTE_SUCCESS,
    component: AddNoteSuccessContainerComponent,
  },
  {
    path: NotesWizardPathsModel.CANCEL_ADD_NOTE,
    component: CancelAddNoteContainerComponent,
  },
  {
    path: NotesWizardPathsModel.DELETE_NOTE,
    component: DeleteNoteContainerComponent,
  },
  {
    path: NotesWizardPathsModel.DELETE_NOTE_SUCCESS,
    component: DeleteNoteSuccessContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TaskNotesRoutingModule {}
