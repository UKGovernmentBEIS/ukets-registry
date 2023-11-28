import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { nextPage } from '../account-holder-contact.actions';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { AccountHolderContactWizardRoutes } from '../account-holder-contact-wizard-properties';
import { MainWizardRoutes } from '../../main-wizard.routes';
import { selectAccountHolderContact } from '../account-holder-contact.selector';

@Component({
  selector: 'app-personal-details-container',
  template: `
    <app-account-holder-contact-details
      [accountHolderContact]="accountHolderContact$ | async"
      [contactType]="contactType"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-contact-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PersonalDetailsContainerComponent implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact> = this.store.select(
    selectAccountHolderContact
  );

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly nextRoute =
    AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_CONTACT_DETAILS;
  readonly overviewRoute = AccountHolderContactWizardRoutes.OVERVIEW;
  contactType: string;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.mainWizardRoute,
        extras: { skipLocationChange: true },
      })
    );
    this.store.dispatch(clearErrors());
    this.contactType = this.route.snapshot.paramMap.get('contactType');
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      nextPage({
        accountHolderContact: value,
        contactType: this.contactType,
      })
    );
    this._router.navigate([this.nextRoute, this.contactType], {
      skipLocationChange: true,
    });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
