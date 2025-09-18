import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { KeycloakUser } from '@shared/user/keycloak-user';

@Component({
  selector: 'app-recovery-methods',
  templateUrl: './recovery-methods.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecoveryMethodsComponent {
  @Input() user: KeycloakUser;
  @Input() initiatorUrid: string;

  @Output() readonly initUpdateRecoveryEmail = new EventEmitter<void>();
  @Output() readonly initRemoveRecoveryEmail = new EventEmitter<void>();
  @Output() readonly initUpdateRecoveryPhone = new EventEmitter<void>();
  @Output() readonly initRemoveRecoveryPhone = new EventEmitter<void>();

  get canEdit(): boolean {
    return this.initiatorUrid === this.user.attributes.urid[0];
  }

  get recoveryCountryCode(): string {
    return this.user.attributes.recoveryCountryCode?.[0];
  }

  get recoveryPhoneNumber(): string {
    return this.user.attributes.recoveryPhoneNumber?.[0];
  }

  get recoveryEmailAddress(): string {
    return this.user.attributes.recoveryEmailAddress?.[0];
  }

  onUpdateRecoveryEmail() {
    this.initUpdateRecoveryEmail.emit();
  }

  onUpdateRecoveryPhone() {
    this.initUpdateRecoveryPhone.emit();
  }

  onRemoveRecoveryEmail() {
    this.initRemoveRecoveryEmail.emit();
  }

  onRemoveRecoveryPhone() {
    this.initRemoveRecoveryPhone.emit();
  }
}
