import { Component, Input, OnInit } from '@angular/core';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectRegistrationConfigurationProperty } from '@shared/shared.selector';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-email-verify-not-received-email',
  templateUrl: './email-verify-not-received-email.component.html',
})
export class EmailVerifyNotReceivedEmailComponent implements OnInit {
  @Input() configuration: Configuration[];
  @Input() serviceDeskWeb: string;
  readonly emailAddressRoute = '/registration/emailAddress';

  serviceDeskEmail$: Observable<string>;
  serviceDeskEmailSubject$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.serviceDeskEmail$ = this.store.select(
      selectRegistrationConfigurationProperty,
      {
        property: 'user.registration.keycloak.emailFrom',
      }
    );

    this.serviceDeskEmailSubject$ = this.store.select(
      selectRegistrationConfigurationProperty,
      {
        property: 'user.registration.keycloak.emailSubject',
      }
    );
  }
}
