import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { TrustedAccountListActions } from '@trusted-account-list/actions';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-cancel-update-request-container',
  template: `
    <app-cancel-update-request
      updateRequestText="trusted account list"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelUpdateRequestContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.pipe(
      tap((params) => {
        this.goBackRoute = params.goBackRoute;
        // {order: "popular"}
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.goBackRoute,
            extras: { skipLocationChange: true },
          })
        );
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      TrustedAccountListActions.cancelTrustedAccountListUpdateRequest()
    );
  }
}
