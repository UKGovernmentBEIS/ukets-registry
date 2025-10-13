import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable, of } from 'rxjs';
import {
  selectAccountHolder,
  selectAccountHolderWizardCompleted,
} from '../account-holder/account-holder.selector';
import {
  selectAccountHolderContact,
  selectAccountHolderContactWizardCompleted,
  selectAlternativeAccountHolderContact,
  selectPrimaryAccountHolderContact,
} from '@account-holder-contact/account-holder-contact.selector';
import { Store } from '@ngrx/store';
import {
  selectAccountType,
  selectIsAOHA,
  selectIsMOHA,
  selectIsOHA,
  selectIsOHAOrAOHA,
  selectIsOHAOrAOHAorMOHA,
  selectMaxNumberOfARs,
  selectMinNumberOfARs,
} from '../account-opening.selector';
import { AccountHolderWizardRoutes } from '../account-holder/account-holder-wizard-properties';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { MainWizardRoutes } from '../main-wizard.routes';
import { filter, map, mergeMap, take } from 'rxjs/operators';
import { AccountDetailsWizardRoutes } from '../account-details/account-details-wizard-properties';
import {
  selectAccountDetails,
  selectAccountDetailsCompleted,
} from '../account-details/account-details.selector';
import {
  ARAccessRights,
  AccountDetails,
  AccountHolder,
  AccountType,
  AccountTypeMap,
  AuthorisedRepresentative,
  RegistryAccountType,
} from '@shared/model/account';
import { TrustedAccountList } from '../trusted-account-list/trusted-account-list';
import {
  selectTrustedAccountList,
  selectTrustedAccountListCompleted,
} from '../trusted-account-list/trusted-account-list.selector';
import { TrustedAccountListWizardRoutes } from '../trusted-account-list/trusted-account-list-wizard-properties';
import {
  selectOperatorCompleted,
  selectOperatorTextForMainWizard,
  selectOperatorWizardLink,
} from '../operator/operator.selector';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import { AccountHolderContactWizardRoutes } from '@account-holder-contact/account-holder-contact-wizard-properties';
import { AuthorisedRepresentativeWizardRoutes } from '../authorised-representative/authorised-representative-wizard-properties';
import {
  selectAuthorisedRepresentatives,
  selectAuthorisedRepresentativesCompleted,
} from '../authorised-representative/authorised-representative.selector';
import {
  clearCurrentAuthorisedRepresentative,
  setAuthorisedRepresentativeIndex,
  setAuthorisedRepresentativeViewOrCheck,
  setCurrentAuthorisedRepresentativeByIndex,
} from '../authorised-representative/authorised-representative.actions';
import { ViewOrCheck } from '../account-opening.model';
import { cancelRequest } from '../account-opening.actions';
import { ErrorDetail } from '@shared/error-summary';
import {
  nextPage,
  removeAccountHolderContact,
  setViewState,
} from '@account-holder-contact/account-holder-contact.actions';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-main-wizard',
  templateUrl: './main-wizard.component.html',
  styleUrls: ['./main-wizard.component.scss'],
})
export class MainWizardComponent implements OnInit {
  maxNumberOfARs: number;

  readonly AccountTypeMap = AccountTypeMap;
  readonly maritimeOperatorLabel: string = 'Maritime Operator Holding Account';

  accountType$: Observable<AccountType> = this.store.select(selectAccountType);

  isOha$: Observable<boolean> = this.store.select(selectIsOHA);

  isAOHA$: Observable<boolean> = this.store.select(selectIsAOHA);

  isMOHA$: Observable<boolean> = this.store.select(selectIsMOHA);

  isOHAorAOHA$: Observable<boolean> = this.store.select(selectIsOHAOrAOHA);

  isOHAorAOHAorMOHA$: Observable<boolean> = this.store.select(
    selectIsOHAOrAOHAorMOHA
  );

  maxNumberOfARs$: Observable<number> = this.store.select(selectMaxNumberOfARs);
  minNumberOfARs$: Observable<number> = this.store.select(selectMinNumberOfARs);

  accountHolder$: Observable<AccountHolder> =
    this.store.select(selectAccountHolder);

  accountHolderCompleted$: Observable<boolean> = this.store.select(
    selectAccountHolderWizardCompleted
  );

  legalRepresentantiveName$: Observable<string>;

  legalRepresentantive$: Observable<AccountHolderContact> = this.store.select(
    selectAccountHolderContact
  );

  primaryContact$: Observable<AccountHolderContact> = this.store.select(
    selectPrimaryAccountHolderContact
  );

  accountHolderContactCompleted$: Observable<boolean> = this.store.select(
    selectAccountHolderContactWizardCompleted
  );

  alternativeContact$: Observable<AccountHolderContact> = this.store.select(
    selectAlternativeAccountHolderContact
  );

  showAuthorisedRepresentativeLink$: Observable<boolean>;

  accountDetails$: Observable<AccountDetails> =
    this.store.select(selectAccountDetails);
  accountDetailsCompleted$: Observable<boolean> = this.store.select(
    selectAccountDetailsCompleted
  );
  trustedAccountList$: Observable<TrustedAccountList> = this.store.select(
    selectTrustedAccountList
  );
  trustedAccountListCompleted$: Observable<boolean> = this.store.select(
    selectTrustedAccountListCompleted
  );

  operatorCompleted$: Observable<boolean> = this.store.select(
    selectOperatorCompleted
  );

  operatorWizardLink$: Observable<string> = this.store.select(
    selectOperatorWizardLink
  );

  operatorTextForMainWizard$: Observable<string> = this.store.select(
    selectOperatorTextForMainWizard
  );
  authorisedRepresentatives$: Observable<AuthorisedRepresentative[]> =
    this.store.select(selectAuthorisedRepresentatives);
  authorisedRepresentativesCompleted$: Observable<boolean> = this.store.select(
    selectAuthorisedRepresentativesCompleted
  );
  submitButtonEnabled$: Observable<boolean>;

  accountHolderWizardRoutes = AccountHolderWizardRoutes;
  accountDetailsWizardRoutes = AccountDetailsWizardRoutes;

  trustedAccountListWizardLink: string;
  accountHolderContactWizardLink: string;
  authorisedRepresentativeSelectionWizardLink =
    AuthorisedRepresentativeWizardRoutes.SELECTION;

  accountHolderSectionCompleted$: Observable<boolean> = combineLatest([
    this.accountHolderCompleted$,
    this.primaryContact$,
  ]).pipe(map(([completed, contact]) => completed && contact !== null));

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(clearErrors());

    this.accountType$.pipe(take(1)).subscribe((accountType) => {
      if (!accountType) {
        this._router.navigate([MainWizardRoutes.ACCOUNT_TYPE], {
          skipLocationChange: true,
          relativeTo: this.route,
        });
      } else {
        if (
          accountType === AccountType.OPERATOR_HOLDING_ACCOUNT ||
          accountType === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ||
          accountType === AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT
        ) {
          this.showAuthorisedRepresentativeLink$ = this.operatorCompleted$;
          this.submitButtonEnabled$ = combineLatest([
            this.accountHolderCompleted$,
            this.accountHolderContactCompleted$,
            this.accountDetailsCompleted$,
            this.operatorCompleted$,
          ]).pipe(map(([a, b, c, d]) => a && b && c && d));
        } else {
          this.showAuthorisedRepresentativeLink$ =
            this.trustedAccountListCompleted$;
          this.submitButtonEnabled$ = combineLatest([
            this.accountHolderCompleted$,
            this.accountHolderContactCompleted$,
            this.accountDetailsCompleted$,
            this.authorisedRepresentativesCompleted$,
          ]).pipe(map(([a, b, c, d]) => a && b && c && d));
        }
      }
    });

    this.maxNumberOfARs$.pipe(take(1)).subscribe((maxNumberOfARs) => {
      this.maxNumberOfARs = maxNumberOfARs;
    });

    this.trustedAccountListCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.trustedAccountListWizardLink =
          TrustedAccountListWizardRoutes.OVERVIEW;
      } else {
        this.trustedAccountListWizardLink =
          TrustedAccountListWizardRoutes.SECOND_APPROVAL_NECESSARY;
      }
    });

    this.legalRepresentantive$
      .pipe(take(1))
      .subscribe((accountHolderContact) => {
        this.accountHolderContactCompleted$
          .pipe(take(1))
          .subscribe((completed) => {
            if (accountHolderContact && completed) {
              this.legalRepresentantiveName$ = of(
                accountHolderContact.details.firstName +
                  ' ' +
                  accountHolderContact.details.lastName +
                  ' (legal representative)'
              );
              this.accountHolderContactWizardLink =
                AccountHolderContactWizardRoutes.OVERVIEW;
            } else {
              this.accountHolderContactWizardLink =
                AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_PERSONAL_DETAILS;
            }
          });
      });
  }

  operatorWizardLink(): Observable<string> {
    return this.operatorWizardLink$.pipe(take(1));
  }

  generateInstallationOrAircraftOperatorText(): Observable<string> {
    return this.operatorTextForMainWizard$.pipe(take(1));
  }

  onContinue() {
    const errorDetails: ErrorDetail[] = this.validate();
    if (errorDetails.length === 0) {
      this._router.navigate([MainWizardRoutes.CHECK_ANSWERS], {
        skipLocationChange: true,
      });
    } else {
      this.store.dispatch(errors({ errorSummary: { errors: errorDetails } }));
    }
  }

  validate(): ErrorDetail[] {
    const errorDetails: ErrorDetail[] = [];
    this.isOHAorAOHA$
      .pipe(
        take(1),
        filter((isOHAorAOHA) => !isOHAorAOHA),
        mergeMap(() => {
          return this.authorisedRepresentatives$.pipe(take(1));
        })
      )
      .subscribe((ars) => {
        this.validateAuthorisedRepresentatives(ars).forEach((error) =>
          errorDetails.push(error)
        );
      });
    return errorDetails;
  }

  validateAuthorisedRepresentatives(
    ars: AuthorisedRepresentative[]
  ): ErrorDetail[] {
    const errorDetails: ErrorDetail[] = [];
    if (ars.length >= 2) {
      const initiatorExists =
        ars
          .map((ar) => ar.right)
          .filter(
            (right) =>
              right == ARAccessRights.INITIATE_AND_APPROVE ||
              right == ARAccessRights.INITIATE
          ).length > 0;
      if (!initiatorExists) {
        errorDetails.push(
          new ErrorDetail(
            null,
            'At least one of the Authorised Representatives needs to have permission to initiate transactions and Trusted Account List (TAL) updates.'
          )
        );
      }
      const approverExists =
        ars
          .map((ar) => ar.right)
          .filter(
            (right) =>
              right == ARAccessRights.INITIATE_AND_APPROVE ||
              right == ARAccessRights.APPROVE
          ).length > 0;
      if (!approverExists) {
        errorDetails.push(
          new ErrorDetail(
            null,
            'At least one of the Authorised Representatives needs to have permission to approve transactions and Trusted Account List (TAL) updates.'
          )
        );
      }
    }
    return errorDetails;
  }

  onAuthorisedRepresentativeClick(i: number) {
    this.store.dispatch(
      setAuthorisedRepresentativeViewOrCheck({ viewOrCheck: ViewOrCheck.VIEW })
    );
    this.store.dispatch(setAuthorisedRepresentativeIndex({ index: i }));
    this.store.dispatch(
      setCurrentAuthorisedRepresentativeByIndex({ index: i })
    );
    this._router.navigate([AuthorisedRepresentativeWizardRoutes.OVERVIEW], {
      skipLocationChange: true,
    });
  }

  onAddAuthorisedRepresentative() {
    this.store.dispatch(
      setAuthorisedRepresentativeViewOrCheck({ viewOrCheck: ViewOrCheck.CHECK })
    );
    this.store.dispatch(setAuthorisedRepresentativeIndex({ index: null }));
    this.store.dispatch(clearCurrentAuthorisedRepresentative());

    this.authorisedRepresentatives$.pipe(take(1)).subscribe((ars) => {
      if (ars.length < this.maxNumberOfARs) {
        this._router.navigate(
          [this.authorisedRepresentativeSelectionWizardLink],
          { skipLocationChange: true }
        );
      } else {
        const errorDetails: ErrorDetail[] = [];
        errorDetails.push(
          new ErrorDetail(
            null,
            'A maximum of ' +
              this.maxNumberOfARs +
              ' authorised representatives can be nominated'
          )
        );
        this.store.dispatch(errors({ errorSummary: { errors: errorDetails } }));
      }
    });
  }

  onCancelRequest() {
    this.store.dispatch(cancelRequest());
    this._router.navigate(['/dashboard']);
  }

  goToAccountHolderContact(
    accountHolderContact: AccountHolderContact,
    contactType: string
  ) {
    this.store.dispatch(setViewState({ view: true }));
    this.store.dispatch(nextPage({ accountHolderContact, contactType }));
    this._router.navigate(
      [AccountHolderContactWizardRoutes.OVERVIEW, contactType],
      { skipLocationChange: true }
    );
  }

  onAddAlternativeContact() {
    this.store.dispatch(removeAccountHolderContact());
    this.store.dispatch(setViewState({ view: false }));
    this._router.navigate(
      [
        AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_PERSONAL_DETAILS,
        ContactType.ALTERNATIVE.valueOf(),
      ],
      { skipLocationChange: true }
    );
  }

  protected readonly RegistryAccountType = RegistryAccountType;
}
