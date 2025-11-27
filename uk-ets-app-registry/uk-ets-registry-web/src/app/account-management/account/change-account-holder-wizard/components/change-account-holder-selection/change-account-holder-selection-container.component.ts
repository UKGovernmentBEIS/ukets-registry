import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
  OnInit,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { AuthModel } from '@registry-web/auth/auth.model';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { selectAllCountries } from '@shared/shared.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  AccountHolder,
  AccountHolderSelectionType,
} from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';
import {
  selectAccountHolderList,
  selectChangeAccountHolderSelectionType,
  selectChangeAccountHolderType,
  selectCurrentAccountHolder,
} from '@change-account-holder-wizard/store/reducers';

@Component({
  selector: 'app-change-account-holder-selection-container',
  template: `
    <app-account-holder-selection
      [accountHolder]="accountHolder$ | async"
      [accountHolderList]="accountHolderList$ | async"
      [accountHolderType]="changeAccountHolderType$ | async"
      [accountHolderSelectionType]="accountHolderSelectionType$ | async"
      [loggedinUser]="loggedinUser$ | async"
      [searchByNameRequestUrl]="searchByNameRequestUrl"
      (errorDetails)="onError($event)"
      (output)="onContinue($event)"
    ></app-account-holder-selection>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAccountHolderSelectionContainerComponent implements OnInit {
  readonly previousRoute = ChangeAccountHolderWizardPathsModel.BASE_PATH;

  searchByNameRequestUrl: string;
  accountHolder$: Observable<AccountHolder>;
  accountHolderList$: Observable<AccountHolder[]>;
  changeAccountHolderType$: Observable<string>;
  accountHolderCompleted$: Observable<boolean>;
  accountHolderSelectionType$: Observable<AccountHolderSelectionType>;
  loggedinUser$: Observable<AuthModel>;
  countries$: Observable<IUkOfficialCountry[]>;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private _router: Router,
    private route: ActivatedRoute,
    private cdref: ChangeDetectorRef,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}`,
      })
    );

    this.searchByNameRequestUrl =
      this.ukEtsRegistryApiBaseUrl +
      '/account-holder.get.by-name-or-identifier';

    this.accountHolderSelectionType$ = this.store.select(
      selectChangeAccountHolderSelectionType
    );

    this.loggedinUser$ = this.store.select(selectLoggedInUser);

    // this.accountHolderCompleted$ = this.store.select(
    //   selectAccountHolderWizardCompleted
    // );

    this.changeAccountHolderType$ = this.store.select(
      selectChangeAccountHolderType
    );

    this.accountHolderList$ = this.store.select(selectAccountHolderList);
    this.accountHolder$ = this.store.select(selectCurrentAccountHolder);
    this.countries$ = this.store.select(selectAllCountries);

    this.changeAccountHolderType$
      .pipe(take(1))
      .subscribe((changeAccountHolderType) => {
        if (!changeAccountHolderType) {
          this._router.navigate([this.previousRoute], {
            skipLocationChange: true,
          });
        }
        this.loggedinUser$.pipe(take(1)).subscribe((loggedInUser) => {
          this.store.dispatch(
            ChangeAccountHolderWizardActions.fetchAccountHolderList({
              holderType: changeAccountHolderType,
            })
          );
        });
      });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onContinue(value: any) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.setAccountHolderSelectionType({
        selectionType: value.accountHolderSelectionType,
        id:
          value.accountHolderSelectionType ===
          AccountHolderSelectionType.FROM_LIST
            ? value.selectedIdFromList
            : value.selectedIdFromSearch,
      })
    );
    if (value.accountHolderSelectionType === AccountHolderSelectionType.NEW) {
      this.store.dispatch(
        ChangeAccountHolderWizardActions.cleanAccountHolderList()
      );
    }
  }
}
