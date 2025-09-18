import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';

@Component({
  selector: 'app-cancel-change-account-holder-container',
  template: `
    <app-cancel-update-request
      updateRequestText="change account holder"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

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
    this.store.dispatch(
      ChangeAccountHolderWizardActions.cancelChangeAccountHolderRequest()
    );
  }
}
