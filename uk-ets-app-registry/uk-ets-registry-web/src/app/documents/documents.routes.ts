import { LoginGuard } from '@registry-web/shared/guards';
import { DocumentsContainerComponent } from './documents-container.component';
import { DocumentsGuard } from './providers/documents.guard';

export const DOCUMENTS_ROUTES = [
  {
    path: '',
    canActivate: [LoginGuard, DocumentsGuard],
    component: DocumentsContainerComponent,
  },
  {
    path: 'wizard',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./documents-wizard/documents-wizard.module').then(
        (m) => m.DocumentsWizardModule
      ),
  },
];
