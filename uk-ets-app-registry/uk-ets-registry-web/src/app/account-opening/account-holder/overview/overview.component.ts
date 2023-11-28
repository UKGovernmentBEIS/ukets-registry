import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UntypedFormBuilder } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAccountHolder,
  selectAccountHolderExisting,
  selectAccountHolderType,
  selectAccountHolderWizardCompleted,
  selectAddressCountry,
  selectCountryOfBirth,
} from '../account-holder.selector';
import { MainWizardRoutes } from '../../main-wizard.routes';
import { AccountHolderWizardRoutes } from '../account-holder-wizard-properties';
import { completeWizard, deleteAccountHolder } from '../account-holder.actions';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { fetchAccountHolderContacts } from '@account-holder-contact/account-holder-contact.actions';
import {
  AccountHolder,
  AccountHolderType,
} from '@shared/model/account/account-holder';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
})
export class OverviewComponent implements OnInit {
  accountHolder$: Observable<AccountHolder> =
    this.store.select(selectAccountHolder);
  accountHolderType$: Observable<AccountHolderType> = this.store.select(
    selectAccountHolderType
  );
  accountHolderAddressCountry$: Observable<string> =
    this.store.select(selectAddressCountry);
  accountHolderCountryOfBirth$: Observable<string> =
    this.store.select(selectCountryOfBirth);
  accountHolderCompleted$: Observable<boolean> = this.store.select(
    selectAccountHolderWizardCompleted
  );
  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectAccountHolderExisting
  );
  accountHolderTypes = AccountHolderType;
  accountHolderWizardRoutes = AccountHolderWizardRoutes;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly accountHolderTypeRoute =
    AccountHolderWizardRoutes.ACCOUNT_HOLDER_TYPE;
  readonly accountHolderSelectionRoute =
    AccountHolderWizardRoutes.ACCOUNT_HOLDER_SELECTION;
  readonly individualDetailsRoute =
    AccountHolderWizardRoutes.INDIVIDUAL_DETAILS;
  readonly individualContactDetailsRoute =
    AccountHolderWizardRoutes.INDIVIDUAL_CONTACT_DETAILS;
  readonly organisationDetailsRoute =
    AccountHolderWizardRoutes.ORGANISATION_DETAILS;
  readonly organisationAddressDetailsRoute =
    AccountHolderWizardRoutes.ORGANISATION_ADDRESS_DETAILS;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private formBuilder: UntypedFormBuilder,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.accountHolder$.pipe(take(1)).subscribe((accountHolder) => {
      if (!accountHolder) {
        this._router.navigate([this.accountHolderTypeRoute], {
          skipLocationChange: true,
        });
      }
    });
    this.accountHolderCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.mainWizardRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.accountHolderExisting$.pipe(take(1)).subscribe((existing) => {
          if (existing) {
            this.store.dispatch(
              canGoBack({
                goBackRoute: this.accountHolderSelectionRoute,
                extras: { skipLocationChange: true },
              })
            );
          } else {
            this.accountHolderType$
              .pipe(take(1))
              .subscribe((accountHolderType) => {
                if (accountHolderType === AccountHolderType.INDIVIDUAL) {
                  this.store.dispatch(
                    canGoBack({
                      goBackRoute: this.individualContactDetailsRoute,
                      extras: { skipLocationChange: true },
                    })
                  );
                } else {
                  this.store.dispatch(
                    canGoBack({
                      goBackRoute: this.organisationAddressDetailsRoute,
                      extras: { skipLocationChange: true },
                    })
                  );
                }
              });
          }
        });
      }
    });
  }

  onApply() {
    this.store.dispatch(completeWizard({ complete: true }));
    this.accountHolder$.pipe(take(1)).subscribe((accountHolder) => {
      if (accountHolder.id) {
        this.store.dispatch(
          fetchAccountHolderContacts({
            accountHolderId: accountHolder.id.toString(),
          })
        );
      }
    });
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  onEdit() {
    this.accountHolderType$.pipe(take(1)).subscribe((accountHolderType) => {
      if (accountHolderType === AccountHolderType.INDIVIDUAL) {
        this._router.navigate([this.individualDetailsRoute], {
          skipLocationChange: true,
        });
      } else {
        this._router.navigate([this.organisationDetailsRoute], {
          skipLocationChange: true,
        });
      }
    });
  }

  onDelete() {
    this.store.dispatch(deleteAccountHolder());
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }
}
