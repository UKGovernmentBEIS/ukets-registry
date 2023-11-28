import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmDeleteFileComponent } from '@delete-file/wizard/components/confirm-delete-file/confirm-delete-file.component';
import { ConfirmDeleteFileContainerComponent } from '@delete-file/wizard/components/confirm-delete-file/confirm-delete-file-container.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DeleteFileRoutingModule } from '@delete-file/delete-file-routing.module';
import { DeleteFileService } from '@delete-file/wizard/services/delete-file.service';
import { StoreModule } from '@ngrx/store';
import * as fromDeleteFileReducer from '@delete-file/wizard/reducers';
import { EffectsModule } from '@ngrx/effects';
import { DeleteFileEffects } from '@delete-file/wizard/effects/delete-file.effects';

@NgModule({
  declarations: [
    ConfirmDeleteFileComponent,
    ConfirmDeleteFileContainerComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    DeleteFileRoutingModule,
    StoreModule.forFeature(
      fromDeleteFileReducer.deleteFileFeatureKey,
      fromDeleteFileReducer.reducer
    ),
    EffectsModule.forFeature([DeleteFileEffects]),
  ],
  providers: [DeleteFileService],
})
export class DeleteFileModule {}
