import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  AccountHolder,
  AccountHolderContact,
  AccountHolderType,
} from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import {
  selectCurrentAccountHolder,
  selectAcquiringAccountHolder,
  selectAcquiringAccountHolderContact,
  selectAcquiringAccountHolderContactAddressCountry,
  selectChangeAccountHolderWizardCompleted,
  selectChangeAccountHolderExisting,
  selectChangeAccountHolderType,
} from '@change-account-holder-wizard/store/reducers';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-change-account-holder-overview-container',
  templateUrl: './change-account-holder-overview-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: ``,
})
export class ChangeAccountHolderOverviewContainerComponent implements OnInit {
  accountIdentifier$: Observable<number>;
  transferringAccountHolder$: Observable<AccountHolder>;
  acquiringAccountHolder$: Observable<AccountHolder>;
  acquiringAccountHolderType$: Observable<AccountHolderType>;
  acquiringAccountHolderContact$: Observable<AccountHolderContact>;
  acquiringAccountHolderContactAddressCountry$: Observable<string>;
  accountHolderExisting$: Observable<boolean>;
  accountHolderCompleted$: Observable<boolean>;
  primaryType = ContactType.PRIMARY;

  individualDetailsRoute: string;
  organisationDetailsRoute: string;
  contactDetailsRoute: string;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.individualDetailsRoute = `/account/${this.route.snapshot.paramMap.get(
      'accountId'
    )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${
      ChangeAccountHolderWizardPathsModel.INDIVIDUAL
    }`;

    this.organisationDetailsRoute = `/account/${this.route.snapshot.paramMap.get(
      'accountId'
    )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${
      ChangeAccountHolderWizardPathsModel.ORGANISATION
    }`;

    this.contactDetailsRoute = `/account/${this.route.snapshot.paramMap.get(
      'accountId'
    )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${
      ChangeAccountHolderWizardPathsModel.PRIMARY_CONTACT
    }`;

    this.transferringAccountHolder$ = this.store.select(
      selectCurrentAccountHolder
    );
    this.acquiringAccountHolder$ = this.store.select(
      selectAcquiringAccountHolder
    );
    this.acquiringAccountHolderType$ = this.store.select(
      selectChangeAccountHolderType
    );
    this.acquiringAccountHolderContact$ = this.store.select(
      selectAcquiringAccountHolderContact
    );
    this.acquiringAccountHolderContactAddressCountry$ = this.store.select(
      selectAcquiringAccountHolderContactAddressCountry
    );
    this.accountHolderExisting$ = this.store.select(
      selectChangeAccountHolderExisting
    );
    this.accountHolderCompleted$ = this.store.select(
      selectChangeAccountHolderWizardCompleted
    );

    this.accountHolderExisting$.subscribe((existing) => {
      if (existing) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: `/account/${this.route.snapshot.paramMap.get(
              'accountId'
            )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${
              ChangeAccountHolderWizardPathsModel.ACCOUNT_HOLDER_SELECTION
            }`,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: `/account/${this.route.snapshot.paramMap.get(
              'accountId'
            )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${
              ChangeAccountHolderWizardPathsModel.PRIMARY_CONTACT_WORK
            }`,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
  }

  onSubmit() {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.submitChangeAccountHolderRequest()
    );
  }

  navigateToAccountHolderDetails() {
    console.log(`Navigating to account holder details.`);
    this.store.dispatch(
      ChangeAccountHolderWizardActions.navigateToAccountHolderDetails()
    );
  }

  navigateToContactDetails() {
    console.log(
      `Navigating to account holder contact details: ${this.contactDetailsRoute}`
    );
    this.store.dispatch(
      ChangeAccountHolderWizardActions.navigateTo({
        route: this.contactDetailsRoute,
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.cancelClicked({
        route: this.route.snapshot['_routerState'].url,
      })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
