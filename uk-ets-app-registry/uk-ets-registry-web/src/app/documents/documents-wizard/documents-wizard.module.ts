import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DOCUMENTS_WIZARD_ROUTES } from './documents-wizard.routes';
import * as fromDocumentsWizard from './store/documents-wizard.reducer';
import { DocumentsWizardEffects } from './store/documents-wizard.effects';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { DocumentsWizardGuard } from './providers/documents-wizard.guard';

@NgModule({
  imports: [
    RouterModule.forChild(DOCUMENTS_WIZARD_ROUTES),
    StoreModule.forFeature(
      fromDocumentsWizard.documentsWizardReducerFeatureKey,
      fromDocumentsWizard.reducer
    ),
    EffectsModule.forFeature([DocumentsWizardEffects]),
  ],
  providers: [DocumentsWizardGuard],
})
export class DocumentsWizardModule {}
