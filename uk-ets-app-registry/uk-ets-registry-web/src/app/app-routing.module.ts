import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { EmptyPageComponent } from '@shared/empty-page/empty-page.component';
import { ClearRequestDocumentsGuard } from '@request-documents/wizard/guards/clear-request-documents-guard.service';
import { SitemapContainerComponent } from '@registry-web/sitemap';
import { TimedOutComponent } from '@registry-web/timeout/timed-out/timed-out.component';
import { ClearBulkArGuard } from '@registry-web/bulk-ar/guards';
import { GuidanceContainerComponent } from '@guidance/components/guidance-container.component';
import { ClearDeleteFileGuard } from '@registry-web/delete-file/wizard/guards/clear-delete-file.guard';
import { NotificationListGuard } from '@notifications/notifications-list/guards';
import { ClearNotificationsResultsGuard } from '@notifications/notifications-list/guards/clear-notifications-results-guard';
import { AboutComponent } from './about/about.component';
import { RecoveryMethodsChangeRoutePaths } from './user-management/recovery-methods-change/recovery-methods-change.models';
import { clearRequestPaymentGuard } from '@request-payment/guards';

const routes: Routes = [
  { path: 'under-construction', component: EmptyPageComponent },
  {
    path: 'registration',
    loadChildren: () =>
      import('./registration/registration.module').then(
        (mod) => mod.RegistrationModule
      ),
  },
  // tasks
  {
    path: 'task-list',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./task-management/task-list/task-list.module').then(
        (mod) => mod.TaskListModule
      ),
  },
  {
    path: 'task-details',
    loadChildren: () =>
      import('./task-management/task-details/task-details.module').then(
        (mod) => mod.TaskDetailsModule
      ),
  },
  {
    path: 'account',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./account-management/account/account.module').then(
        (mod) => mod.AccountModule
      ),
  },
  {
    path: 'dashboard',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./dashboard/dashboard.module').then((mod) => mod.DashboardModule),
  },
  {
    path: 'account-opening',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./account-opening/account-opening.module').then(
        (m) => m.AccountOpeningModule
      ),
  },
  {
    path: 'bulk-ar',
    canActivate: [LoginGuard],
    canDeactivate: [ClearBulkArGuard],
    loadChildren: () =>
      import('./bulk-ar/bulk-ar.module').then((m) => m.BulkArModule),
  },
  {
    path: 'user-list',
    loadChildren: () =>
      import('./user-management/user-list/user-list.module').then(
        (m) => m.UserListModule
      ),
  },
  {
    path: 'user-details',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./user-management/user-details/user-details.module').then(
        (m) => m.UserDetailsModule
      ),
  },
  {
    path: 'account-list',
    loadChildren: () =>
      import('./account-management/account-list/account-list.module').then(
        (m) => m.AccountListModule
      ),
  },
  {
    path: 'transaction-list',
    loadChildren: () =>
      import(
        './transaction-management/transaction-list/transaction-list.module'
      ).then((m) => m.TransactionListModule),
  },
  {
    path: 'transaction-details',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import(
        './transaction-management/transaction-details/transaction-details.module'
      ).then((mod) => mod.TransactionDetailsModule),
  },
  // TODO: move path to kp-administration
  {
    path: 'kpadministration',
    loadChildren: () =>
      import('./kp-administration/kp-administration.module').then(
        (m) => m.KpAdministrationModule
      ),
  },
  {
    path: 'ets-administration',
    loadChildren: () =>
      import('./ets-administration/ets-administration.module').then(
        (m) => m.EtsAdministrationModule
      ),
  },
  {
    path: 'request-documents',
    canDeactivate: [ClearRequestDocumentsGuard],
    loadChildren: () =>
      import('./request-documents/request-documents.module').then(
        (m) => m.RequestDocumentsModule
      ),
  },
  {
    path: 'request-payment',
    canDeactivate: [clearRequestPaymentGuard],
    loadChildren: () =>
      import('./request-payment/request-payment.module').then(
        (m) => m.RequestPaymentModule
      ),
  },
  {
    path: 'delete-file',
    canDeactivate: [ClearDeleteFileGuard],
    loadChildren: () =>
      import('./delete-file/delete-file.module').then(
        (m) => m.DeleteFileModule
      ),
  },
  {
    path: 'email-change',
    loadChildren: () =>
      import('./user-management/email-change/email-change.module').then(
        (m) => m.EmailChangeModule
      ),
  },
  {
    path: RecoveryMethodsChangeRoutePaths.BASE_PATH,
    canActivate: [LoginGuard],
    loadChildren: () =>
      import(
        './user-management/recovery-methods-change/recovery-methods-change.module'
      ).then((m) => m.RecoveryMethodsChangeModule),
  },
  {
    path: 'password-change',
    loadChildren: () =>
      import('./user-management/password-change/password-change.module').then(
        (m) => m.PasswordChangeModule
      ),
  },
  {
    path: 'forgot-password',
    loadChildren: () =>
      import('./forgot-password/forgot-password.module').then(
        (mod) => mod.ForgotPasswordModule
      ),
  },

  {
    path: 'token-change',
    loadChildren: () =>
      import('@user-management/token-change/token-change.module').then(
        (m) => m.TokenChangeModule
      ),
  },
  {
    path: 'emergency-otp-change',
    loadChildren: () =>
      import(
        './user-management/emergency-otp-change/emergency-otp-change.module'
      ).then((m) => m.EmergencyOtpChangeModule),
  },
  {
    path: 'emergency-password-otp-change',
    loadChildren: () =>
      import(
        './user-management/emergency-password-otp-change/emergency-password-otp-change.module'
      ).then((m) => m.EmergencyPasswordOtpChangeModule),
  },
  {
    path: 'sitemap',
    component: SitemapContainerComponent,
  },
  {
    path: 'registry-administration',
    loadChildren: () =>
      import('./registry-administration/registry-administration.module').then(
        (m) => m.RegistryAdministrationModule
      ),
  },
  {
    path: 'notifications',
    canActivate: [LoginGuard, NotificationListGuard],
    canDeactivate: [ClearNotificationsResultsGuard],
    loadChildren: () =>
      import(
        './notifications/notifications-list/notifications-list.module'
      ).then((m) => m.NotificationsListModule),
  },
  {
    path: 'guidance',
    canActivate: [LoginGuard],
    component: GuidanceContainerComponent,
    loadChildren: () =>
      import('./guidance/guidance.module').then((m) => m.GuidanceModule),
  },
  {
    path: 'reports',
    loadChildren: () =>
      import('./reports/reports.module').then((m) => m.ReportsModule),
  },
  {
    path: 'timed-out',
    component: TimedOutComponent,
  },
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full',
  },
  {
    path: 'about',
    component: AboutComponent,
    title: 'About',
  },
  {
    path: 'documents',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./documents/documents.module').then((mod) => mod.DocumentsModule),
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      anchorScrolling: 'enabled',
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
