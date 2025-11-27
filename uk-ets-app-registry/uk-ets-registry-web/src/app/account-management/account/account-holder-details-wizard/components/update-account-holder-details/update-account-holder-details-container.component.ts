import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { selectAllCountries } from '@shared/shared.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AccountHolder, AccountHolderType } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { selectUpdatedAccountHolder } from '../../reducers/account-holder-details.selector';
import {
  cancelClicked,
  setAccountHolderDetails,
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';

@Component({
  selector: 'app-update-account-holder-details-container',
  template: `
    <app-account-holder-individual-details
      [isAHUpdateWizard]="true"
      *ngIf="(accountHolder$ | async)?.type === individual"
      [caption]="'Request to update the account holder'"
      [header]="'Update the account holder details'"
      [accountHolder]="accountHolder$ | async"
      [countries]="countries$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-individual-details>
    <app-account-holder-organisation-details
      [isAHUpdateWizard]="true"
      *ngIf="(accountHolder$ | async)?.type === organisation"
      [caption]="'Request to update the account holder'"
      [header]="'Update the account holder details'"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-organisation-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateAccountHolderDetailsContainerComponent implements OnInit {
  countries$: Observable<IUkOfficialCountry[]>;
  accountHolder$: Observable<AccountHolder>;

  organisation = AccountHolderType.ORGANISATION;
  individual = AccountHolderType.INDIVIDUAL;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountHolderDetailsWizardPathsModel.BASE_PATH}/${
          AccountHolderDetailsWizardPathsModel.SELECT_UPDATE_TYPE
        }`,
      })
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.accountHolder$ = this.store.select(selectUpdatedAccountHolder);
  }

  onContinue(value: AccountHolder) {
    this.store.dispatch(
      setAccountHolderDetails({
        accountHolderInfoChanged: {
          details: value.details,
        },
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
