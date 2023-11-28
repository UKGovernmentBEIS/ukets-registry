import { AfterViewInit, Component, OnInit } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import {
  selectAccountHolder,
  selectAccountHolderExisting,
  selectAccountHolderType,
  selectAccountHolderWizardCompleted,
  selectAddressCountry,
  selectCountryOfBirth,
} from '../account-holder/account-holder.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectAccountHolderContactWizardCompleted } from '@account-holder-contact/account-holder-contact.selector';
import {
  AccessRightLabelHintMap,
  AccountDetails,
  AccountHolder,
  AccountType,
  AccountTypeMap,
  getRuleLabel,
  operatorTypeMap,
} from '@shared/model/account';
import {
  selectAccountDetails,
  selectAccountDetailsCompleted,
  selectAccountDetailsCountry,
} from '../account-details/account-details.selector';
import {
  selectTrustedAccountList,
  selectTrustedAccountListCompleted,
} from '../trusted-account-list/trusted-account-list.selector';
import { TrustedAccountList } from '../trusted-account-list/trusted-account-list';
import {
  AircraftOperator,
  Installation,
  InstallationActivityType,
  Operator,
  OperatorType,
  Regulator,
} from '../../shared/model/account/operator';
import {
  selectAircraftOperator,
  selectInstallation,
  selectInstallationPermitDate,
  selectInstallationToBeTransferred,
  selectOperator,
  selectOperatorCompleted,
  selectOperatorType,
} from '../operator/operator.selector';
import { AuthorisedRepresentative } from '@shared/model/account';
import {
  selectAuthorisedRepresentatives,
  selectAuthorisedRepresentativesCompleted,
} from '../authorised-representative/authorised-representative.selector';
import { map, take } from 'rxjs/operators';
import { MainWizardRoutes } from '../main-wizard.routes';
import {
  selectAccountType,
  selectIsOHAOrAOHA,
} from '../account-opening.selector';
import { canGoBack, errors } from '@shared/shared.action';
import { createAccount } from '../account-opening.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { initAll } from 'govuk-frontend';
import {
  AccountHolderType,
  Individual,
} from '../../shared/model/account/account-holder';
import { UkDate } from '@shared/model/uk-date';
import { AuthorisedRepresentativeWizardRoutes } from '../authorised-representative/authorised-representative-wizard-properties';
import {
  setAuthorisedRepresentativeIndex,
  setAuthorisedRepresentativeViewOrCheck,
  setCurrentAuthorisedRepresentativeByIndex,
} from '@account-opening/authorised-representative/authorised-representative.actions';
import { ViewOrCheck } from '@account-opening/account-opening.model';
import { AccountHolderWizardRoutes } from '@account-opening/account-holder/account-holder-wizard-properties';
import { AccountDetailsWizardRoutes } from '@account-opening/account-details/account-details-wizard-properties';
import { TrustedAccountListWizardRoutes } from '@account-opening/trusted-account-list/trusted-account-list-wizard-properties';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import {
  completeWizard,
  nextPage,
} from '@account-opening/account-holder/account-holder.actions';
import { regulatorMap } from '@account-management/account-list/account-list.model';

@Component({
  selector: 'app-check-answers',
  templateUrl: './check-answers.component.html',
})
export class CheckAnswersComponent implements OnInit, AfterViewInit {
  accountType$: Observable<AccountType> = this.store.select(selectAccountType);
  accountHolder$: Observable<Individual> = this.store.select(
    selectAccountHolder
  ) as Observable<Individual>;
  accountHolderCompleted$: Observable<boolean> = this.store.select(
    selectAccountHolderWizardCompleted
  );
  accountHolderType$: Observable<AccountHolderType> = this.store.select(
    selectAccountHolderType
  );
  accountHolderAddressCountry$: Observable<string> =
    this.store.select(selectAddressCountry);
  accountHolderCountryOfBirth$: Observable<string> =
    this.store.select(selectCountryOfBirth);

  accountHolderContactCompleted$: Observable<boolean> = this.store.select(
    selectAccountHolderContactWizardCompleted
  );
  accountDetails$: Observable<AccountDetails> =
    this.store.select(selectAccountDetails);
  accountDetailsCountry$: Observable<string> = this.store.select(
    selectAccountDetailsCountry
  );
  accountDetailsCompleted$: Observable<boolean> = this.store.select(
    selectAccountDetailsCompleted
  );
  trustedAccountList$: Observable<TrustedAccountList> = this.store.select(
    selectTrustedAccountList
  );
  trustedAccountListCompleted$: Observable<boolean> = this.store.select(
    selectTrustedAccountListCompleted
  );
  operator$: Observable<Operator> = this.store.select(selectOperator);
  type = OperatorType;
  installationToBeTransferred$: Observable<Installation> = this.store.select(
    selectInstallationToBeTransferred
  );
  operatorType$: Observable<OperatorType> =
    this.store.select(selectOperatorType);
  operatorCompleted$: Observable<boolean> = this.store.select(
    selectOperatorCompleted
  );
  installation$: Observable<Installation> =
    this.store.select(selectInstallation);
  installationPermitDate$: Observable<UkDate> = this.store.select(
    selectInstallationPermitDate
  );
  aircraftOperator$: Observable<AircraftOperator> = this.store.select(
    selectAircraftOperator
  );
  authorisedRepresentatives$: Observable<AuthorisedRepresentative[]> =
    this.store.select(selectAuthorisedRepresentatives);
  authorisedRepresentativesCompleted$: Observable<boolean> = this.store.select(
    selectAuthorisedRepresentativesCompleted
  );
  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectAccountHolderExisting
  );

  completeInformation$: Observable<boolean>;

  isOHAorAOHA$: Observable<boolean> = this.store.select(selectIsOHAOrAOHA);

  accountTypeText: string;
  accountHolderTypes = AccountHolderType;
  operatorType = OperatorType;
  installationOrAircraftOperatorText: string;
  activityTypes = InstallationActivityType;
  mainWizardRoutes = MainWizardRoutes;
  authorisedRepresentativeOverviewWizardLink =
    AuthorisedRepresentativeWizardRoutes.ACCESS_RIGHTS;
  accountHolderOverviewWizardLink = AccountHolderWizardRoutes.OVERVIEW;
  accountHolderWizardLinkIndividual =
    AccountHolderWizardRoutes.INDIVIDUAL_DETAILS;
  accountHolderWizardLinkIndividualContact =
    AccountHolderWizardRoutes.INDIVIDUAL_CONTACT_DETAILS;
  accountHolderWizardLinkOrganisation =
    AccountHolderWizardRoutes.ORGANISATION_DETAILS;
  accountHolderWizardLinkOrganisationAddress =
    AccountHolderWizardRoutes.ORGANISATION_ADDRESS_DETAILS;
  accountDetailsWizardLink = AccountDetailsWizardRoutes.ACCOUNT_DETAILS;
  trustedAccountListWizardLinkSecondApproval =
    TrustedAccountListWizardRoutes.SECOND_APPROVAL_NECESSARY;
  trustedAccountListWizardLinkOutsideList =
    TrustedAccountListWizardRoutes.TRANSFERS_OUTSIDE_LIST_ALLOWED;
  singlepersonsurrenderexcessallocation =
    TrustedAccountListWizardRoutes.SINGLE_PERSON_SURRENDER_EXCESS_ALLOCATION;
  operatorWizardLinkInstallation = OperatorWizardRoutes.INSTALLATION;
  operatorWizardLinkAircraft = OperatorWizardRoutes.AIRCRAFT_OPERATOR;
  billingDetailsWizardLink = AccountDetailsWizardRoutes.BILLING_DETAILS;
  operatorTypeMap = operatorTypeMap;
  accessRightLabelHintMap = AccessRightLabelHintMap;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.completeInformation$ = combineLatest([
      this.accountHolderCompleted$,
      this.accountHolderContactCompleted$,
      this.accountDetailsCompleted$,
      this.trustedAccountListCompleted$,
    ]).pipe(map(([a, b, c, d]) => a && b && c && d));
    this.store.dispatch(
      canGoBack({
        goBackRoute: MainWizardRoutes.TASK_LIST,
        extras: { skipLocationChange: true },
      })
    );
    this.accountType$.pipe(take(1)).subscribe((accountType) => {
      this.accountTypeText = AccountTypeMap[accountType].label;

      if (accountType === AccountType.OPERATOR_HOLDING_ACCOUNT) {
        this.completeInformation$ = combineLatest([
          this.completeInformation$,
          this.operatorCompleted$,
        ]).pipe(map(([a, b]) => a && b));
        this.installationOrAircraftOperatorText =
          operatorTypeMap[OperatorType.INSTALLATION];
      } else if (
        accountType === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
      ) {
        this.completeInformation$ = combineLatest([
          this.completeInformation$,
          this.operatorCompleted$,
        ]).pipe(map(([a, b]) => a && b));
        this.installationOrAircraftOperatorText =
          operatorTypeMap[OperatorType.AIRCRAFT_OPERATOR];
      } else {
        this.completeInformation$ = combineLatest([
          this.completeInformation$,
          this.authorisedRepresentativesCompleted$,
        ]).pipe(map(([a, b]) => a && b));
      }
      this.completeInformation$.pipe(take(1)).subscribe((complete) => {
        if (!complete) {
          this.store.dispatch(
            errors({ errorSummary: this.incompleteInformationError() })
          );
        }
      });
    });
  }

  ngAfterViewInit(): void {
    initAll();
  }

  getRegulatorText(regulator: Regulator) {
    return regulatorMap[regulator];
  }

  getAccessRightsText(ar: AuthorisedRepresentative): string {
    return this.accessRightLabelHintMap.get(ar.right)?.text || 'Read only';
  }

  incompleteInformationError(): ErrorSummary {
    return new ErrorSummary([
      new ErrorDetail(
        null,
        'The request is incomplete. Please revise by going back to the wizard.'
      ),
    ]);
  }

  onContinue() {
    this.store.dispatch(createAccount());
  }

  navigateTo(route) {
    this._router.navigate([route], { skipLocationChange: true });
  }

  onAuthorisedRepresentativeClick(i: number) {
    this.store.dispatch(
      setAuthorisedRepresentativeViewOrCheck({ viewOrCheck: ViewOrCheck.VIEW })
    );
    this.store.dispatch(setAuthorisedRepresentativeIndex({ index: i }));
    this.store.dispatch(
      setCurrentAuthorisedRepresentativeByIndex({ index: i })
    );
    this._router.navigate([this.authorisedRepresentativeOverviewWizardLink], {
      skipLocationChange: true,
    });
  }

  onAddressDetailsClick(value: AccountHolder, path: string) {
    this.store.dispatch(completeWizard({ complete: false }));
    this.store.dispatch(
      nextPage({
        accountHolder: value,
      })
    );
    this._router.navigate([path], { skipLocationChange: true });
  }

  getTransactionRuleValue(rule: boolean | null) {
    return getRuleLabel(rule);
  }
}
