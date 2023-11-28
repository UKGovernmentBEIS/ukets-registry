import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { SetAuthorityUserFormComponent } from '@authority-setting/component';
import { SetAuthorityUserFormContainerComponent } from '@authority-setting/component';
import { SetAuthorityUserWizardComponent } from '@authority-setting/component';
import {
  CheckAuthorityUserComponent,
  CheckAuthorityUserContainerComponent,
  SetAuthorityUserSuccessComponent
} from '@authority-setting/component';
import { AuthoritySettingStoreModule } from '@authority-setting/authority-setting-store.module';
import { AuthoritySettingRoutingModule } from '@authority-setting/authority-setting-routing.module';
import { CancelAuthoritySettingComponent } from '@authority-setting/component/cancel-authority-setting';

@NgModule({
  declarations: [
    SetAuthorityUserFormComponent,
    SetAuthorityUserFormContainerComponent,
    SetAuthorityUserWizardComponent,
    SetAuthorityUserSuccessComponent,
    CheckAuthorityUserComponent,
    CheckAuthorityUserContainerComponent,
    CancelAuthoritySettingComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    AuthoritySettingRoutingModule,
    AuthoritySettingStoreModule
  ]
})
export class AuthoritySettingModule {}
