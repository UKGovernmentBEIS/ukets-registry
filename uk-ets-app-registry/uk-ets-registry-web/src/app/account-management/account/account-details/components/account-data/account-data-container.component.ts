import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { clearErrors, clearGoBackRoute } from '@shared/shared.action';
import { Observable } from 'rxjs';
import {
  Account,
  AccountHoldingsResult,
  TrustedAccount,
} from '@shared/model/account';
import {
  selectAccount,
  selectAccountHolderFiles,
  selectAccountHoldings,
  selectHasOperatorUpdatePendingApproval,
  selectIsKyotoAccountType,
  selectIsOHAOrAOHA,
  selectSideMenuItems,
  selectTrustedAccountDescriptionOrFullIdentifier,
} from '../../account.selector';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectConfigurationRegistry,
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@shared/shared.selector';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { AccountDataComponent } from '@account-management/account/account-details/components/account-data/account-data.component';
import {
  fetchAccountHolderFile,
  navigateToUpdateAccountAllocationWizard,
  prepareForTrustedAccountChangeDescription,
  submitIncludeBilling,
} from '@account-management/account/account-details/account.actions';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { enterRequestDocumentsWizard } from '@request-documents/wizard/actions';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { FileDetails } from '@shared/model/file/file-details.model';
import { isSeniorAdmin } from '@registry-web/auth/auth.selector';
import { AccountComplianceSelector } from '@account-management/account/account-details/store/reducers';
import {
  clearDeleteFileName,
  enterDeleteFileWizard,
} from '@registry-web/delete-file/wizard/actions/delete-file.actions';
import { selectFileName } from '@registry-web/delete-file/wizard/reducers';
import { isCancelPendingActivationSuccessAndNotLoading } from '@trusted-account-list/reducers';
import { Configuration } from '@shared/configuration/configuration.interface';
import { GoBackNavigationExtras } from '@registry-web/shared/back-button';
import { selectIsReportSuccess } from '@reports/selectors';
import {
  selectAccountHolderNotes,
  selectAccountNotes,
} from 'src/app/notes/store/notes.selector';
import { Note } from '@registry-web/shared/model/note';

@Component({
  selector: 'app-account-data-container',
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-feature-header-wrapper>
      <app-account-header
        [account]="account$ | async"
        [accountHeaderActionsVisibility]="true"
        [showBackToList]="true"
        [goBackToListRoute]="goBackToListRoute$ | async"
        [goBackToListNavigationExtras]="goBackToListNavigationExtras$ | async"
        [isReportSuccess]="isReportSuccess$ | async"
      >
      </app-account-header>
    </app-feature-header-wrapper>
    <app-account-data
      [account]="account$ | async"
      [documents]="accountHolderFiles$ | async"
      [accountId]="accountId"
      [countries]="countries$ | async"
      [sideMenuItems]="sideMenuItems$ | async"
      [isAdmin]="isAdmin$ | async"
      [isSeniorAdmin]="isSeniorAdmin$ | async"
      [isOHAOrAOHA]="isOHAOrAOHA$ | async"
      [isKyotoAccountType]="isKyotoAccountType$ | async"
      [isReadOnlyAdmin]="isReadOnlyAdmin$ | async"
      [accountHoldingsResult]="accountHoldingsResult$ | async"
      [compliantEntityIdentifier]="compliantEntityIdentifier$ | async"
      [hasOperatorUpdatePendingApproval]="
        hasOperatorUpdatePendingApproval$ | async
      "
      [currentMenuItem]="currentMenuItem"
      [fileDeleted]="fileDeleted$ | async"
      [isCancelPendingActivationSuccessDisplayed]="
        isCancelPendingActivationSuccessDisplayed$ | async
      "
      [selectedTrustedAccountDescription]="
        selectedTrustedAccountDescription$ | async
      "
      [configuration]="configuration$ | async"
      [accountNotes]="accountNotes$ | async"
      [accountHolderNotes]="accountHolderNotes$ | async"
      (selectedItem)="onSelectMenuItem($event)"
      (updateAllocationStatus)="onUpdateAllocationStatus()"
      (requestDocuments)="onAccountHolderRequestDocuments($event)"
      (downloadAHFileEmitter)="downloadAccountHolderFile($event)"
      (removeAHFileEmitter)="removeAccountHolderFile($event)"
      (trustedAccountFullIdentifierDescriptionUpdate)="
        loadTrustedAccountUpdateDescription($event)
      "
      (includeInBilling)="includeInBilling()"
    ></app-account-data>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountDataContainerComponent implements OnInit {
  account$: Observable<Account>;
  countries$: Observable<IUkOfficialCountry[]>;
  isAdmin$: Observable<boolean>;
  isSeniorAdmin$: Observable<boolean>;
  isReadOnlyAdmin$: Observable<boolean>;
  isOHAOrAOHA$: Observable<boolean>;
  isKyotoAccountType$: Observable<boolean>;
  sideMenuItems$: Observable<string[]>;
  accountHolderFiles$: Observable<FileDetails[]>;
  accountHoldingsResult$: Observable<AccountHoldingsResult>;
  compliantEntityIdentifier$: Observable<number>;
  hasOperatorUpdatePendingApproval$: Observable<boolean>;
  fileDeleted$: Observable<string>;
  isCancelPendingActivationSuccessDisplayed$: Observable<boolean>;
  selectedTrustedAccountDescription$: Observable<string>;
  configuration$: Observable<Configuration[]>;
  accountId: string;
  currentMenuItem: string;
  goBackToListRoute$: Observable<string>;
  goBackToListNavigationExtras$: Observable<GoBackNavigationExtras>;
  isReportSuccess$: Observable<boolean>;
  accountNotes$: Observable<Note[]>;
  accountHolderNotes$: Observable<Note[]>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private store: Store,
    private authApiService: AuthApiService
  ) {}

  ngOnInit(): void {
    this.isOHAOrAOHA$ = this.store.select(selectIsOHAOrAOHA);
    this.isKyotoAccountType$ = this.store.select(selectIsKyotoAccountType);
    this.accountHolderFiles$ = this.store.select(selectAccountHolderFiles);
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.account$ = this.store.select(selectAccount);
    this.sideMenuItems$ = this.store.select(selectSideMenuItems);
    this.countries$ = this.store.select(selectAllCountries);
    this.isAdmin$ = this.authApiService.hasScope(
      'urn:uk-ets-registry-api:actionForAnyAdmin'
    );
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
    this.hasOperatorUpdatePendingApproval$ = this.store.select(
      selectHasOperatorUpdatePendingApproval
    );
    this.accountHoldingsResult$ = this.store.select(selectAccountHoldings);
    this.compliantEntityIdentifier$ = this.store.select(
      AccountComplianceSelector.selectCompliantEntityIdentifier
    );
    this.fileDeleted$ = this.store.select(selectFileName);

    this.isCancelPendingActivationSuccessDisplayed$ = this.store.select(
      isCancelPendingActivationSuccessAndNotLoading
    );
    this.selectedTrustedAccountDescription$ = this.store.select(
      selectTrustedAccountDescriptionOrFullIdentifier
    );

    this.configuration$ = this.store.select(selectConfigurationRegistry);

    // show banner for 3 seconds
    setTimeout(() => this.store.dispatch(clearDeleteFileName()), 3000);

    this.isReadOnlyAdmin$ = this.authApiService.hasScope(
      'urn:uk-ets-registry-api:actionForReadOnlyAdmins'
    );
    this.store.dispatch(clearGoBackRoute());
    this.goBackToListRoute$ = this.store.select(selectGoBackToListRoute);

    this.goBackToListNavigationExtras$ = this.store.select(
      selectGoBackToListNavigationExtras
    );

    this.isReportSuccess$ = this.store.select(selectIsReportSuccess);
    this.accountNotes$ = this.store.select(selectAccountNotes);
    this.accountHolderNotes$ = this.store.select(selectAccountHolderNotes);
  }

  onSelectMenuItem(selectedMenuItem: string): void {
    if (selectedMenuItem === AccountDataComponent.START_ITEM) {
      this.store.dispatch(clearErrors());
    }
    this.currentMenuItem = selectedMenuItem;
  }

  onUpdateAllocationStatus(): void {
    this.store.dispatch(
      navigateToUpdateAccountAllocationWizard({
        accountId: this.accountId,
      })
    );
  }

  onAccountHolderRequestDocuments({
    accountName,
    accountHolderId,
    accountHolderName,
    accountFullIdentifier,
  }): void {
    this.store.dispatch(
      enterRequestDocumentsWizard({
        origin: RequestDocumentsOrigin.ACCOUNT_DETAILS,
        originatingPath: this.router.url,
        documentsRequestType: DocumentsRequestType.ACCOUNT_HOLDER,
        accountHolderIdentifier: accountHolderId,
        accountHolderName,
        accountName,
        accountFullIdentifier,
      })
    );
  }

  downloadAccountHolderFile(file: FileDetails): void {
    this.store.dispatch(
      fetchAccountHolderFile({
        fileId: file.id,
      })
    );
  }

  loadTrustedAccountUpdateDescription(account: TrustedAccount): void {
    this.store.dispatch(
      prepareForTrustedAccountChangeDescription({
        accountFullIdentifier: account.accountFullIdentifier,
        accountDescription: account.description,
      })
    );
  }

  removeAccountHolderFile({ file, id }) {
    this.store.dispatch(
      enterDeleteFileWizard({
        originatingPath: this.router.url,
        id,
        file,
        documentsRequestType: DocumentsRequestType.ACCOUNT_HOLDER,
      })
    );
  }

  includeInBilling() {
    this.store.dispatch(submitIncludeBilling());
  }
}
