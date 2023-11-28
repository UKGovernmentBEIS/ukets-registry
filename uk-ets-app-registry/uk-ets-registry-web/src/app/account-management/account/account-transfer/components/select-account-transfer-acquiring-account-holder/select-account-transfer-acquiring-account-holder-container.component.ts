import {
  ChangeDetectionStrategy,
  Component,
  Inject,
  OnInit,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import {
  cancelClicked,
  setAccountTransferType,
} from '@account-transfer/store/actions/account-transfer.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  AccountTransferType,
  SelectedAccountTransferType,
} from '@account-transfer/model';
import { Observable } from 'rxjs';
import {
  selectAccountTransferType,
  selectAcquiringAccountHolderIdentifier,
  selectCurrentAccountHolderIdentifier,
} from '@account-transfer/store/reducers';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

@Component({
  selector: 'app-select-account-transfer-acquiring-account-holder-container',
  template: `
    <app-select-account-transfer-acquiring-account-holder
      [updateType]="updateType$ | async"
      [selectedAcquiringAccountHolderIdentifier]="
        selectedAcquiringAccountHolderIdentifier$ | async
      "
      [currentAccountHolderIdentifier]="currentAccountHolderIdentifier$ | async"
      [searchByIdentifierRequestUrl]="searchByIdentifierRequestUrl"
      (selectedAccountTransferType)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-select-account-transfer-acquiring-account-holder>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectAccountTransferAcquiringAccountHolderContainerComponent
  implements OnInit {
  updateType$: Observable<AccountTransferType>;
  selectedAcquiringAccountHolderIdentifier$: Observable<number>;
  currentAccountHolderIdentifier$: Observable<number>;
  searchByIdentifierRequestUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.searchByIdentifierRequestUrl =
      this.ukEtsRegistryApiBaseUrl + '/account-holder.get.by-identifier';
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.updateType$ = this.store.select(selectAccountTransferType);
    this.selectedAcquiringAccountHolderIdentifier$ = this.store.select(
      selectAcquiringAccountHolderIdentifier
    );
    this.currentAccountHolderIdentifier$ = this.store.select(
      selectCurrentAccountHolderIdentifier
    );
  }

  onCancel(): void {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onContinue(selectedAccountTransferType: SelectedAccountTransferType): void {
    // without this, the error summary is still visible when clicking the continue button (and the form is valid)
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      setAccountTransferType({
        selectedAccountTransferType,
      })
    );
  }

  onError(value: ErrorDetail[]): void {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
