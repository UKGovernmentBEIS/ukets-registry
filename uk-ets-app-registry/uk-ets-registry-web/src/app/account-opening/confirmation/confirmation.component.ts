import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  selectAccountType,
  selectRequestID,
} from '../account-opening.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  AccountHolderContact,
  AccountHolderContactInfo,
} from '@shared/model/account/account-holder-contact';
import { selectAccountHolderContactInfo } from '@account-holder-contact/account-holder-contact.selector';
import {
  AccountType,
  AccountTypeMap,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { selectAuthorisedRepresentatives } from '../authorised-representative/authorised-representative.selector';
import { AuthModel } from '../../auth/auth.model';
import { selectLoggedInUser } from '../../auth/auth.selector';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@shared/shared.selector';
import { getConfigurationValue } from '@shared/shared.util';
import { fetchAccountOpeningSummaryFile } from '../account-opening.actions';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.scss'],
})
export class ConfirmationComponent implements OnInit {
  emails: string[] = [];
  requestID$: Observable<number> = this.store.select(selectRequestID);
  accountHolderContactInfo$: Observable<AccountHolderContactInfo> =
    this.store.select(selectAccountHolderContactInfo);
  authorisedRepresentatives$: Observable<AuthorisedRepresentative[]> =
    this.store.select(selectAuthorisedRepresentatives);
  loggedInUser$: Observable<AuthModel> = this.store.select(selectLoggedInUser);
  configuration$: Observable<Configuration[]> = this.store.select(
    selectConfigurationRegistry
  );
  accountType$: Observable<AccountType> = this.store.select(selectAccountType);

  accountTypeText: string;
  serviceDeskEmail: string;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.loggedInUser$.pipe(take(1)).subscribe((user) => {
      this.emails.push(' ' + user.username + ' ');
    });

    this.accountHolderContactInfo$
      .pipe(take(1))
      .subscribe((accountHolderContactInfo) => {
        function addEmail(accountHolderContact: AccountHolderContact) {
          if (accountHolderContact) {
            this.emails.push(
              ' ' + accountHolderContact.emailAddress.emailAddress + ' '
            );
          }
        }
        addEmail.call(this, accountHolderContactInfo.primaryContact);
        addEmail.call(this, accountHolderContactInfo.alternativeContact);
      });

    this.authorisedRepresentatives$.pipe(take(1)).subscribe((ars) => {
      for (const ar of ars) {
        this.emails.push(' ' + ar.user.emailAddress + ' ');
      }
    });

    this.emails = this.emails.filter(
      (array, i, arr) => arr.findIndex((t) => t === array) === i
    );

    this.configuration$.pipe(take(1)).subscribe((configuration) => {
      this.serviceDeskEmail = getConfigurationValue(
        'mail.etrAddress',
        configuration
      );
    });

    this.accountType$
      .pipe(take(1))
      .subscribe(
        (accountType) =>
          (this.accountTypeText = AccountTypeMap[accountType]?.label)
      );
  }

  downloadCopy() {
    this.store.dispatch(fetchAccountOpeningSummaryFile());
  }
}
