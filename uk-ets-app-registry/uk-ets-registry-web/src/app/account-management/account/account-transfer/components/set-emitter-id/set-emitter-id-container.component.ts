import { Component, OnInit } from '@angular/core';
import {
  cancelClicked,
  setAcquiringEmitterId,
} from '@account-transfer/store/actions/account-transfer.actions';
import { ErrorSummary, ErrorDetail } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAcquiringEmitterId,
  selectCompliantEntityIdentifier,
} from '@account-transfer/store/reducers';
import {
  AccountTransferPathsModel,
  SelectedEmitterType,
} from '@account-transfer/model';

@Component({
  selector: 'app-set-emitter-id-container',
  template: `
    <app-set-emitter-id
      [compliantEntityIdentifier]="compliantEntityIdentifier$ | async"
      [emitterId]="emitterId$ | async"
      (selectedEmitterId)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-set-emitter-id>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: ``,
})
export class SetEmitterIdContainerComponent implements OnInit {
  compliantEntityIdentifier$!: Observable<number>;
  emitterId$!: Observable<string>;

  constructor(
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.compliantEntityIdentifier$ = this.store.select(
      selectCompliantEntityIdentifier
    );
    this.emitterId$ = this.store.select(selectAcquiringEmitterId);

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
  }

  onContinue(acquiringEmitter: SelectedEmitterType): void {
    this.store.dispatch(
      setAcquiringEmitterId({
        acquiringEmitterId: acquiringEmitter.selectedEmitterId,
      })
    );
  }

  onError(value: ErrorDetail[]): void {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }

  onCancel(): void {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
