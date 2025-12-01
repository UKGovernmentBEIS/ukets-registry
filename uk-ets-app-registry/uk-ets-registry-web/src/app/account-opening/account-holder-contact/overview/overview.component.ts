import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAccountHolderContact,
  selectAccountHolderContactView,
  selectAccountHolderContactWizardCompleted,
  selectAddressCountry,
} from '../account-holder-contact.selector';
import { MainWizardRoutes } from '../../main-wizard.routes';
import { AccountHolderContactWizardRoutes } from '../account-holder-contact-wizard-properties';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import {
  completeWizard,
  deleteAccountHolderContact,
  setViewState,
} from '../account-holder-contact.actions';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
})
export class OverviewComponent implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact> = this.store.select(
    selectAccountHolderContact
  );

  accountHolderContactCompleted$: Observable<boolean> = this.store.select(
    selectAccountHolderContactWizardCompleted
  );

  selectAccountHolderContactView$: Observable<boolean> = this.store.select(
    selectAccountHolderContactView
  );

  accountHolderAddressCountry$: Observable<string> =
    this.store.select(selectAddressCountry);
  legelRepresentativeWizardRoutes = AccountHolderContactWizardRoutes;

  contactType: string;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly accountHolderContactPersonPersonalDetailsRoute =
    AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_PERSONAL_DETAILS;
  readonly accountHolderContactContactdetailsRoute =
    AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_CONTACT_DETAILS;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.contactType = this.route.snapshot.paramMap.get('contactType');
    this.store.dispatch(clearErrors());
    this.selectAccountHolderContactView$
      .pipe(take(1))
      .subscribe((accountHolderContactView) => {
        if (accountHolderContactView) {
          this.store.dispatch(
            canGoBack({
              goBackRoute: this.mainWizardRoute,
              extras: { skipLocationChange: true },
            })
          );
        }
      });
    this.accountHolderContactCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.mainWizardRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.accountHolderContact$.pipe(take(1)).subscribe((legalRep) => {
          if (legalRep && legalRep.id) {
            this.store.dispatch(
              canGoBack({
                goBackRoute: this.mainWizardRoute,
                extras: { skipLocationChange: true },
              })
            );
          } else {
            this.store.dispatch(
              canGoBack({
                goBackRoute: `${this.accountHolderContactContactdetailsRoute}/${this.contactType}`,
                extras: { skipLocationChange: true },
              })
            );
          }
        });
      }
    });
  }

  onApply() {
    this.store.dispatch(completeWizard({ contactType: this.contactType }));
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  onEdit() {
    this.store.dispatch(setViewState({ view: false }));
    this._router.navigate(
      [this.accountHolderContactPersonPersonalDetailsRoute, this.contactType],
      { skipLocationChange: true }
    );
  }

  onDelete() {
    this.store.dispatch(
      deleteAccountHolderContact({ contactType: this.contactType })
    );
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  getTitle(contactView: boolean): string {
    return `${contactView ? 'View ' : 'Check '} your answers`;
  }

  getDetailsTitle(): string {
    return this.contactType === ContactType.ALTERNATIVE
      ? 'Alternative Primary Contact details'
      : 'Primary Contact details';
  }

  getWorkDetailsTitle(): string {
    return this.contactType === ContactType.ALTERNATIVE
      ? 'Alternative Primary Contact work details'
      : 'Primary Contact work details';
  }

  getTypeText() {
    return this.contactType === ContactType.ALTERNATIVE
      ? 'alternative Primary Contact'
      : 'Primary Contact';
  }
}
