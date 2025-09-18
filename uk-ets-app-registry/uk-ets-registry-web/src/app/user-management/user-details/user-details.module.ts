import { NgModule } from '@angular/core';
import { UserDetailsRoutingModule } from './user-details.routes';
import { UserDetailsEffect } from './store/effects';
import * as fromUserDetails from './store/reducers';
import { UserHeaderGuard } from '@user-management/guards/user-header.guard';
import { UserDetailService } from '@user-management/service';
import { RequestDocumentsModule } from '@request-documents/request-documents.module';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { EmailChangeStoreModule } from '@email-change/email-change-store.module';
import {
  ArInAccountsComponent,
  ArInAccountsContainerComponent,
  IdentificationDocumentationComponent,
  PersonalDetailsComponent,
  RegistrationDetailsComponent,
  UserDetailsComponent,
  UserDetailsContainerComponent,
  WorkContactDetailsComponent,
} from '@user-management/user-details/components';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { TokenChangeModule } from '@user-management/token-change/token-change.module';
import { PasswordChangeModule } from '@user-management/password-change/password-change.module';
import { UserHeaderComponent } from '@user-management/headers/user-header/user-header.component';
import { DeleteFileModule } from '@registry-web/delete-file/delete-file.module';
import { KeycloakUserDisplayNamePipe } from '@registry-web/shared/pipes';
import { RecoveryMethodsComponent } from './components/recovery-methods/recovery-methods.component';
import { RecoveryMethodsChangeModule } from '../recovery-methods-change/recovery-methods-change.module';

@NgModule({
  declarations: [
    RegistrationDetailsComponent,
    RecoveryMethodsComponent,
    PersonalDetailsComponent,
    WorkContactDetailsComponent,
    IdentificationDocumentationComponent,
    ArInAccountsComponent,
    ArInAccountsContainerComponent,
    UserDetailsComponent,
    UserDetailsContainerComponent,
    UserHeaderComponent,
  ],
  imports: [
    EmailChangeStoreModule,
    UserDetailsRoutingModule,
    TokenChangeModule,
    PasswordChangeModule,
    CommonModule,
    SharedModule,
    RequestDocumentsModule,
    DeleteFileModule,
    StoreModule.forFeature(
      fromUserDetails.userDetailsFeatureKey,
      fromUserDetails.reducer
    ),
    EffectsModule.forFeature([UserDetailsEffect]),
    RecoveryMethodsChangeModule,
  ],
  exports: [UserDetailsContainerComponent, UserHeaderComponent],
  providers: [
    UserDetailService,
    ExportFileService,
    UserHeaderGuard,
    KeycloakUserDisplayNamePipe,
  ],
})
export class UserDetailsModule {}
