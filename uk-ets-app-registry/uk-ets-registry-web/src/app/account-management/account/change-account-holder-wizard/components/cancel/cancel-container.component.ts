import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-cancel-change-account-holder-container',
  template: `
    <app-cancel-update-request
      updateRequestText="change account holder"
      (cancelRequest)="onCancel()"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(
    private readonly store: Store,
    private readonly destroyRef: DestroyRef,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((params) => {
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
      ChangeAccountHolderWizardActions.CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST()
    );
  }
}
