import { Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { MenuItemEnum } from '@account-management/account/account-details/model';
import {
  TableColumn,
  MultiSelectedItem,
} from '@shared/components/multi-select-table';
import { ErrorSummary, ErrorDetail } from '@shared/error-summary';
import {
  claimInvitationStatusMap,
  MetsContact,
  RegistryContact,
} from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { take } from 'rxjs';
import {
  ChangeContactInvitationWizardActions,
  selectMetsContacts,
  selectRegistryContacts,
  selectReturnToOverview,
} from '@change-contact-invitation-wizard/store';
import { CHANGE_CONTACT_INVITATION_BASE_PATH } from '@change-contact-invitation-wizard/change-contact-invitation-wizard-routing.module';

@Component({
  selector: 'app-select-contacts-container',
  templateUrl: './select-contacts-container.component.html',
  styles: ``,
})
export class SelectContactsContainerComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(Store);

  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );

  claimInvitationStatusMap = claimInvitationStatusMap;

  readonly metsContactsColumns: TableColumn[] = [
    { field: 'fullName', header: 'Full name' },
    { field: 'email', header: 'Email' },
    { field: 'phoneNumber', header: 'Phone number' },
    { field: 'contactTypes', header: 'Contact type' },
    { field: 'operatorType', header: 'Operator type' },
    { field: 'invitedOn', header: 'Invited' },
    { field: 'invitationStatus', header: 'Invitation status' },
  ];
  readonly metsContacts = toSignal(this.store.select(selectMetsContacts));

  readonly registryContactsColumns: TableColumn[] = [
    { field: 'fullName', header: 'Full name' },
    { field: 'email', header: 'Email' },
    { field: 'phoneNumber', header: 'Phone number' },
    { field: 'contactType', header: 'Contact type' },
    { field: 'invitedOn', header: 'Invited' },
    { field: 'invitationStatus', header: 'Invitation status' },
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
                goBackRoute: `${goBackRoute}/${CHANGE_CONTACT_INVITATION_BASE_PATH}/overview`,
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
      ChangeContactInvitationWizardActions.setSelectedContacts({
        metsContactsWithSelectState: data,
        registryContactsWithSelectState: this.registryContacts(),
      })
    );
  }

  onRegistryContactsSelectionChanged(
    data: MultiSelectedItem<RegistryContact>[]
  ) {
    this.store.dispatch(
      ChangeContactInvitationWizardActions.setSelectedContacts({
        metsContactsWithSelectState: this.metsContacts(),
        registryContactsWithSelectState: data,
      })
    );
  }

  onContinue() {
    if (
      this.hasSelectedContacts(this.metsContacts(), this.registryContacts())
    ) {
      this.store.dispatch(
        ChangeContactInvitationWizardActions.completeSelectedContacts()
      );
    } else {
      this.onError(
        'Select the contacts whose invitation status you want to change.'
      );
    }
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeContactInvitationWizardActions.cancel({ route }));
  }

  private onError(message: string) {
    const summary: ErrorSummary = { errors: [new ErrorDetail(null, message)] };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  hasSelectedContacts = (
    metsContactsWithSelectState: MultiSelectedItem<MetsContact>[],
    registryContactsWithSelectState: MultiSelectedItem<RegistryContact>[]
  ): boolean =>
    !!metsContactsWithSelectState.find((contact) => contact.isSelected) ||
    !!registryContactsWithSelectState.find((contact) => contact.isSelected);
}
