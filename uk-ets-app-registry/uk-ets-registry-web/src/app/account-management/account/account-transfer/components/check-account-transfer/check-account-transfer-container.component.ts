import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountHolder, AccountHolderContact } from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import {
  AccountTransferPathsModel,
  AcquiringAccountHolderInfo,
} from '@account-transfer/model';
import {
  cancelClicked,
  submitUpdateRequest,
} from '@account-transfer/store/actions/account-transfer.actions';
import {
  selectAcquiringAccountHolder,
  selectAcquiringAccountHolderPrimaryContact,
  selectTransferringAccountHolder,
} from '@account-transfer/store/reducers';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { selectAllCountries } from '@shared/shared.selector';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { AccountTransferActions } from '@account-transfer/store/actions';

@Component({
  selector: 'app-check-account-transfer-container',
  template: `<app-check-account-transfer
      [acquiringAccountHolder]="acquiringAccountHolder$ | async"
      [acquiringAccountHolderPrimaryContact]="
        acquiringAccountHolderPrimaryContact$ | async
      "
      [transferringAccountHolder]="transferringAccountHolder$ | async"
      (errorDetails)="onError($event)"
      (submitRequest)="onSubmit($event)"
      (clickChange)="navigateTo($event)"
    ></app-check-account-transfer>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [],
})
export class CheckAccountTransferContainerComponent implements OnInit {
  countries$: Observable<IUkOfficialCountry[]>;
  acquiringAccountHolder$: Observable<AccountHolder>;
  acquiringAccountHolderPrimaryContact$: Observable<AccountHolderContact>;
  transferringAccountHolder$: Observable<AccountHolder>;
  primaryContactType = ContactType.PRIMARY;
  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountTransferPathsModel.BASE_PATH}/${
          this.route.snapshot.data.goBackPath
        }`,
        extras: { skipLocationChange: true },
      })
    );
    this.acquiringAccountHolder$ = this.store.select(
      selectAcquiringAccountHolder
    );
    this.acquiringAccountHolderPrimaryContact$ = this.store.select(
      selectAcquiringAccountHolderPrimaryContact
    );
    this.transferringAccountHolder$ = this.store.select(
      selectTransferringAccountHolder
    );
    this.countries$ = this.store.select(selectAllCountries);
  }

  onCancel(): void {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onSubmit(acquiringAccountHolderInfo: AcquiringAccountHolderInfo): void {
    this.store.dispatch(submitUpdateRequest({ acquiringAccountHolderInfo }));
  }

  navigateTo(routePath: AccountTransferPathsModel): void {
    console.log(`Navigating to : ${routePath}`);
    this.store.dispatch(
      AccountTransferActions.navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get('accountId')}/${
          AccountTransferPathsModel.BASE_PATH
        }/${routePath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onError(value: ErrorDetail): void {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
