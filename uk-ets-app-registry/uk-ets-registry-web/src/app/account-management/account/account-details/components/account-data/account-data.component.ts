import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Account,
  AccountAllocation,
  AccountHoldingsResult,
  ARAccessRights,
  TrustedAccount,
} from '@shared/model/account';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { MenuItemEnum } from '@account-management/account/account-details/model/account-side-menu.model';
import { FileDetails } from '@shared/model/file/file-details.model';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';
import { BannerType } from '@registry-web/shared/banner/banner-type.enum';
import { Store } from '@ngrx/store';
import { TrustedAccountListActions } from '@trusted-account-list/actions';
import { clearAccountDetailsUpdate } from '@account-management/account/account-details/account.actions';
import { Configuration } from '@shared/configuration/configuration.interface';
import { AccountTransferPathsModel } from '@account-transfer/model';
import { Note } from '@registry-web/shared/model/note';
import { Observable } from 'rxjs';
import { selectAccountAllocation } from '@account-management/account/account-details/account.selector';

@Component({
  selector: 'app-account-data',
  templateUrl: './account-data.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountDataComponent implements OnInit {
  static readonly START_ITEM = MenuItemEnum.OVERVIEW;

  private _account: Account;
  private _isAdmin: boolean;
  private _isReadOnlyAdmin: boolean;
  private _notesCounter = 0;

  @Input()
  set account(account: Account) {
    this._account = account;
  }

  get account(): Account {
    return this._account;
  }

  @Input()
  set isAdmin(isAdmin: boolean) {
    this._isAdmin = isAdmin;
  }

  get isAdmin(): boolean {
    return this._isAdmin;
  }

  @Input()
  set isReadOnlyAdmin(isReadonlyAdmin: boolean) {
    this._isReadOnlyAdmin = isReadonlyAdmin;
  }

  get isReadOnlyAdmin(): boolean {
    return this._isReadOnlyAdmin;
  }

  @Input() set accountNotes(val: Note[]) {
    this.accountNotesArr = val;
    this.updateNotesCounter();
  }

  @Input() set accountHolderNotes(val: Note[]) {
    this.accountHolderNotesArr = val;
    this.updateNotesCounter();
  }

  @Input() isSeniorAdmin: boolean;
  @Input() isSeniorOrJuniorAdmin: boolean;
  @Input() isOHAOrAOHAorMOHA: boolean;
  @Input() isKyotoAccountType: boolean;
  @Input() countries: IUkOfficialCountry[];
  @Input() accountNotesArr: Note[];
  @Input() accountHolderNotesArr: Note[];

  @Input() accountId: string;
  @Input() sideMenuItems: string[];
  @Input() currentMenuItem: string;
  @Input() documents: FileDetails[];
  @Input() hasOperatorUpdatePendingApproval: boolean;
  @Input() accountHoldingsResult: AccountHoldingsResult;
  @Input() compliantEntityIdentifier: number;
  @Input() fileDeleted: string;
  @Input() isCancelPendingActivationSuccessDisplayed: boolean;
  @Input() selectedTrustedAccountDescription: string;
  @Input() configuration: Configuration[];

  @Output() readonly selectedItem = new EventEmitter<string>();
  @Output() readonly updateAllocationStatus = new EventEmitter();
  @Output() readonly downloadAHFileEmitter = new EventEmitter<FileDetails>();
  @Output() readonly removeAHFileEmitter = new EventEmitter<FileDetails>();
  @Output() readonly requestDocuments = new EventEmitter<{
    accountName: string;
    accountFullIdentifier: string;
    accountHolderId: number;
    accountHolderName: string;
  }>();
  @Output()
  readonly trustedAccountFullIdentifierDescriptionUpdate =
    new EventEmitter<TrustedAccount>();
  @Output() readonly includeInBilling = new EventEmitter();

  readonly menuItems = MenuItemEnum;
  readonly ArAccessRights = ARAccessRights;
  readonly BannerType = BannerType;
  selectedTab: string;
  accountAllocation$: Observable<AccountAllocation>;
  sideMenuItemLabels: Map<string, string> = new Map<string, string>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {
    this.selectedTab =
      this.router.getCurrentNavigation()?.extras?.state?.selectedSideMenu;
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const selectedSideMenu = params['selectedSideMenu'] ?? this.selectedTab;

      this.currentMenuItem = selectedSideMenu
        ? selectedSideMenu
        : AccountDataComponent.START_ITEM;
    });

    this.accountAllocation$ = this.store.select(selectAccountAllocation);
  }

  onSelectMenuItem(currentMenuItem: string): void {
    this.selectedItem.emit(currentMenuItem);
    this.currentMenuItem = currentMenuItem;
    // When the back button in 'Update the account details' page is clicked (to return to account overview page),
    // the updated values should be reset
    this.store.dispatch(clearAccountDetailsUpdate());
    this.store.dispatch(
      TrustedAccountListActions.hideCancelPendingActivationSuccessBanner()
    );
  }

  onUpdateAllocationStatus(): void {
    this.updateAllocationStatus.emit();
  }

  onAccountHolderRequestDocuments(): void {
    this.requestDocuments.emit({
      accountName: this.account.accountDetails.name,
      accountHolderId: this.account.accountHolder.id,
      accountHolderName: this.account.accountHolder.details.name,
      accountFullIdentifier: this.account.accountDetails.accountNumber,
    });
  }

  downloadAccountHolderFile(file: FileDetails): void {
    this.downloadAHFileEmitter.emit(file);
  }

  removeAccountHolderFile(file: FileDetails): void {
    this.removeAHFileEmitter.emit(file);
  }

  loadTrustedAccountUpdateDescription(trustedAccount: TrustedAccount): void {
    this.trustedAccountFullIdentifierDescriptionUpdate.emit(trustedAccount);
  }

  goToRequestOperatorUpdate(): void {
    this.router.navigate([
      this.route.snapshot['_routerState'].url +
        `/${OperatorUpdateWizardPathsModel.BASE_PATH}/${OperatorUpdateWizardPathsModel.CONFIRM_UPDATE}`,
    ]);
  }

  goToRequestTalUpdate() {
    this.router.navigate([
      this.route.snapshot['_routerState'].url + '/trusted-account-list',
    ]);
  }

  goToRequestTransferAccount(): void {
    this.router.navigate([
      this.route.snapshot['_routerState'].url +
        `/${AccountTransferPathsModel.BASE_PATH}`,
    ]);
  }

  includeInBillingClick() {
    this.includeInBilling.emit();
  }

  updateNotesCounter() {
    if (this.accountNotesArr && this.accountHolderNotesArr) {
      this._notesCounter =
        this.accountNotesArr.length + this.accountHolderNotesArr.length;
      if (this._notesCounter > 0) {
        this.sideMenuItemLabels.set(
          'Notes',
          'Notes (' + this._notesCounter + ')'
        );
      }
    }
  }
}
