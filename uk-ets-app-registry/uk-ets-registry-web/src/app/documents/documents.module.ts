import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DOCUMENTS_ROUTES } from './documents.routes';
import { DocumentsGuard } from './providers/documents.guard';
import { DocumentsEffects } from './store/documents.effects';
import * as fromDocuments from './store/documents.reducer';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

@NgModule({
  imports: [
    RouterModule.forChild(DOCUMENTS_ROUTES),
    StoreModule.forFeature(
      fromDocuments.documentsReducerFeatureKey,
      fromDocuments.reducer
    ),
    EffectsModule.forFeature([DocumentsEffects]),
  ],
  providers: [DocumentsGuard],
})
export class DocumentsModule {}
