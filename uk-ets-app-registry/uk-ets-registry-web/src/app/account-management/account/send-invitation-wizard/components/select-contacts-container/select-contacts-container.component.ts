import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Store } from '@ngrx/store';
import {
  MultiSelectedItem,
  MultiSelectTableComponent,
  TableColumn,
} from '@registry-web/shared/components/multi-select-table';
import {
  MetsContactOperatorTypePipe,
  MetsContactTypePipe,
  RegistryContactTypePipe,
} from '@registry-web/shared/pipes';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  SendInvitationActions,
  selectMetsContacts,
  selectRegistryContacts,
  selectReturnToOverview,
} from '@send-invitation-wizard/store';
import { ActivatedRoute, Router } from '@angular/router';
import { take } from 'rxjs';
import { canGoBack, errors } from '@registry-web/shared/shared.action';
import {
  hasSelectedContacts,
  SEND_INVITATION_BASE_PATH,
  SendInvitationWizardPaths,
} from '@send-invitation-wizard/send-invitation-wizard.helpers';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import {
  MetsContact,
  RegistryContact,
} from '@registry-web/shared/model/account';

@Component({
  selector: 'app-select-contacts-container',
  templateUrl: './select-contacts-container.component.html',
  standalone: true,
  imports: [
    MultiSelectTableComponent,
    SharedModule,
    MetsContactTypePipe,
    MetsContactOperatorTypePipe,
    RegistryContactTypePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectContactsContainerComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(Store);

  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );

  readonly metsContactsColumns: TableColumn[] = [
    { field: 'fullName', header: 'Full name' },
    { field: 'email', header: 'Email' },
    { field: 'phoneNumber', header: 'Phone number' },
    { field: 'contactTypes', header: 'Contact type' },
    { field: 'operatorType', header: 'Operator type' },
    { field: 'invitedOn', header: 'Invited' },
  ];
  readonly metsContacts = toSignal(this.store.select(selectMetsContacts));

  readonly registryContactsColumns: TableColumn[] = [
    { field: 'fullName', header: 'Full name' },
    { field: 'email', header: 'Email' },
    { field: 'phoneNumber', header: 'Phone number' },
    { field: 'contactType', header: 'Contact type' },
    { field: 'invitedOn', header: 'Invited' },
  ];
  readonly registryContacts = toSignal(
    this.store.select(selectRegistryContacts)
  );

  ngOnInit() {
    this.returnToOverview$.pipe(take(1)).subscribe((returnToOverview) => {
      const goBackRoute = `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}`;
      this.store.dispatch(
        canGoBack(
          returnToOverview
            ? {
                goBackRoute: `${goBackRoute}/${SEND_INVITATION_BASE_PATH}/${
                  SendInvitationWizardPaths.OVERVIEW
                }`,
                extras: { skipLocationChange: true },
              }
            : {
                goBackRoute,
                extras: {
                  queryParams: {
                    selectedSideMenu: MenuItemEnum.CONTACTS,
                  },
                },
              }
        )
      );
    });
  }

  onMetsContactsSelectionChanged(data: MultiSelectedItem<MetsContact>[]) {
    this.store.dispatch(
      SendInvitationActions.SET_SELECTED_CONTACTS({
        metsContactsWithSelectState: data,
        registryContactsWithSelectState: this.registryContacts(),
      })
    );
  }

  onRegistryContactsSelectionChanged(
    data: MultiSelectedItem<RegistryContact>[]
  ) {
    this.store.dispatch(
      SendInvitationActions.SET_SELECTED_CONTACTS({
        metsContactsWithSelectState: this.metsContacts(),
        registryContactsWithSelectState: data,
      })
    );
  }

  onContinue() {
    if (hasSelectedContacts(this.metsContacts(), this.registryContacts())) {
      this.store.dispatch(SendInvitationActions.COMPLETE_SELECTED_CONTACTS());
    } else {
      this.onError('Select who will receive the invitation');
    }
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(SendInvitationActions.CANCEL({ route }));
  }

  private onError(message: string) {
    const summary: ErrorSummary = { errors: [new ErrorDetail(null, message)] };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
