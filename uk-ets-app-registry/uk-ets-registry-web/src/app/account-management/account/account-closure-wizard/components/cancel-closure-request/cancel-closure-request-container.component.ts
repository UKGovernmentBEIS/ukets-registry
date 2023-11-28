import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { cancelAccountClosureRequest } from '@account-management/account/account-closure-wizard/actions';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-cancel-closure-request-container',
  template: ` <div
      appScreenReaderPageAnnounce
      [pageTitle]="'Cancel account closure request'"
    ></div>

    <app-cancel-update-request
      [notification]="
        'Are you sure you want to cancel the account closure and return back to the account page?'
      "
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelClosureRequestContainerComponent implements OnInit {
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
    this.store.dispatch(cancelAccountClosureRequest());
  }
}
