import { Component, computed, inject, input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Account, claimInvitationStatusMap } from '@shared/model/account';
import { SEND_INVITATION_BASE_PATH } from '@send-invitation-wizard/send-invitation-wizard.helpers';
import { AUTO_CLAIM_BASE_PATH } from '@auto-claim-wizard/auto-claim-wizard-routing.module';
import { BannerType } from '@shared/banner/banner-type.enum';
import { CHANGE_CONTACT_INVITATION_BASE_PATH } from '@change-contact-invitation-wizard/change-contact-invitation-wizard-routing.module';

@Component({
  selector: 'app-account-contacts',
  templateUrl: './account-contacts.component.html',
})
export class AccountContactsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly account = input.required<Account>();
  readonly isSeniorOrJuniorAdmin = input<boolean>(false);
  readonly isSeniorAdmin = input<boolean>(false);
  readonly autoSendInvitationStatusUpdated = input<boolean>(false);

  readonly metsContacts = computed(() => this.account()?.metsContacts || []);
  readonly registryContacts = computed(
    () => this.account()?.registryContacts || []
  );

  accountClaimInvitationStatusMap = claimInvitationStatusMap;

  readonly BannerType = BannerType;

  onSendInvitation() {
    this.router.navigate([SEND_INVITATION_BASE_PATH], {
      relativeTo: this.activatedRoute,
    });
  }

  onChangeContactInvitationStatus() {
    this.router.navigate([CHANGE_CONTACT_INVITATION_BASE_PATH], {
      relativeTo: this.activatedRoute,
    });
  }

  navigateToChangeAutoClaim() {
    this.router.navigate([AUTO_CLAIM_BASE_PATH], {
      relativeTo: this.activatedRoute,
    });
  }
}
