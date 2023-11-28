import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { AccountTransferActions } from '@account-transfer/store/actions';

@Component({
  selector: 'app-cancel-account-transfer-container',
  template: `
    <app-cancel-update-request
      updateRequestText="account transfer"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelAccountTransferContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel(): void {
    this.store.dispatch(AccountTransferActions.cancelAccountTransferRequest());
  }
}
