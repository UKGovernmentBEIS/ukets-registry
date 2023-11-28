import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { selectUpdateType } from '@trusted-account-list/reducers';
import {
  cancelClicked,
  setRequestUpdateType
} from '@trusted-account-list/actions/trusted-account-list.actions';
import { TrustedAccountListUpdateType } from '@trusted-account-list/model';
import { Observable } from 'rxjs';
import {ErrorDetail, ErrorSummary} from '@shared/error-summary';

@Component({
  selector: 'app-select-type-container',
  template: `
    <app-select-type
      [updateType]="updateType$ | async"
      (cancel)="onCancel($event)"
      (selectUpdateType)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-type>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectTypeContainerComponent implements OnInit {
  updateType$: Observable<TrustedAccountListUpdateType>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get('accountId')}`
      })
    );
    this.updateType$ = this.store.select(selectUpdateType);
  }

  onContinue(updateType: TrustedAccountListUpdateType) {
    this.store.dispatch(setRequestUpdateType({ updateType }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel(route: string) {
    this.store.dispatch(cancelClicked({ route }));
  }
}
