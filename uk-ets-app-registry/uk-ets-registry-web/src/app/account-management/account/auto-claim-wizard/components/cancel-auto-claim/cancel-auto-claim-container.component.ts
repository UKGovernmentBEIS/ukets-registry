import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { take } from 'rxjs';
import { Store } from '@ngrx/store';
import { AutoClaimWizardActions } from '@auto-claim-wizard/store';

@Component({
  selector: 'app-cancel-auto-claim-container',
  template: ` <app-cancel-auto-claim (cancelRequest)="onCancel()" />`,
  styles: ``,
})
export class CancelAutoClaimContainerComponent implements OnInit {
  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.activatedRoute.queryParams.pipe(take(1)).subscribe((params) => {
      this.store.dispatch(
        canGoBack({
          goBackRoute: params.goBackRoute,
          extras: { skipLocationChange: true },
        })
      );
    });
  }

  onCancel(): void {
    this.store.dispatch(AutoClaimWizardActions.cancelRequest());
  }
}
