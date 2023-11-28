import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectionComponent, SelectionContainerComponent } from './selection';
import { OverviewComponent } from './overview/overview.component';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { AUTHORISED_REPRESENTATIVE_ROUTES } from './authorized-representative.routes';
import { EffectsModule } from '@ngrx/effects';
import { AuthorisedRepresentativeEffects } from './authorised-representative.effect';
import { AccessRightsComponent } from './access-rights/access-rights.component';
import { AccessRightsContainerComponent } from './access-rights/access-rights-container.component';

@NgModule({
  declarations: [
    SelectionComponent,
    SelectionContainerComponent,
    AccessRightsComponent,
    AccessRightsContainerComponent,
    OverviewComponent,
  ],
  imports: [
    RouterModule.forChild(AUTHORISED_REPRESENTATIVE_ROUTES),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
    EffectsModule.forFeature([AuthorisedRepresentativeEffects]),
  ],
})
export class AuthorisedRepresentativeModule {}
