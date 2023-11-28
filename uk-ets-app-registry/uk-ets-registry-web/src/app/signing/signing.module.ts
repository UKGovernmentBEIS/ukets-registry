import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import * as fromSigningReducer from './reducers/signing.reducer';
import { ReactiveFormsModule } from '@angular/forms';
import { SigningEffects } from './effects/signing.effects';
import { SignRequestFormComponent } from '@signing/components';

@NgModule({
  declarations: [SignRequestFormComponent],
  imports: [
    ReactiveFormsModule,
    SharedModule,
    CommonModule,
    StoreModule.forFeature(
      fromSigningReducer.signingKey,
      fromSigningReducer.reducer
    ),
    EffectsModule.forFeature([SigningEffects]),
    RouterModule,
    NgbModule,
    NgxPageScrollCoreModule
  ],
  exports: [SignRequestFormComponent]
})
export class SigningModule {}
