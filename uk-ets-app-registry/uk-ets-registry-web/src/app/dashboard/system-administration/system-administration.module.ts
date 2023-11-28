import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SYSTEM_ADMINISTRATION_ROUTES } from './system-administration.routes';
import { SharedModule } from '../../shared/shared.module';
import { ResetDatabaseComponent } from './components/reset-database/reset-database.component';
import { SystemAdministrationEffects } from './store/effects';
import { EffectsModule } from '@ngrx/effects';
import * as fromSystemAdministration from './store/reducers';
import { StoreModule } from '@ngrx/store';

@NgModule({
  declarations: [ResetDatabaseComponent],
  imports: [
    RouterModule.forChild(SYSTEM_ADMINISTRATION_ROUTES),
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromSystemAdministration.systemAdministrationFeatureKey,
      fromSystemAdministration.reducer
    ),
    EffectsModule.forFeature([SystemAdministrationEffects])
  ]
})
export class SystemAdministrationModule {}
