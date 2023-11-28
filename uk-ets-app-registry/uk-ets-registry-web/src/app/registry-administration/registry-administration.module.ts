import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { AuthoritySettingStoreModule } from '@authority-setting/authority-setting-store.module';
import { RegistryAdministrationRoutingModule } from './registry-administration-routing.module';
import {
  RegistryAdministrationComponent,
  RegistryAdministrationContainerComponent
} from './component';

@NgModule({
  declarations: [
    RegistryAdministrationComponent,
    RegistryAdministrationContainerComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    RegistryAdministrationRoutingModule,
    AuthoritySettingStoreModule
  ]
})
export class RegistryAdministrationModule {}
