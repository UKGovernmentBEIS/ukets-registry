import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { UserUpdateDetailsType } from '@user-update/model';
import {
  cancelClicked,
  setRequestUpdateType,
} from '@user-update/action/user-details-update.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import {
  selectIsLoadedFromMyProfilePage,
  selectUserDetailsUpdateType,
} from '@user-update/reducers';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-select-type-user-details-update-container',
  template: `<app-select-type-user-details-update
    [updateType]="updateType$ | async"
    [isMyProfilePage]="isMyProfilePage$ | async"
    (cancelEmitter)="onCancel()"
    (selectUpdateType)="onContinue($event)"
    (errorDetails)="onError($event)"
  ></app-select-type-user-details-update>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectTypeUserDetailsUpdateContainerComponent
  implements OnInit, OnDestroy {
  updateType$: Observable<UserUpdateDetailsType>;
  isMyProfilePage$: Observable<boolean>;

  private subscription: Subscription = null;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.subscription = this.store
      .select(selectIsLoadedFromMyProfilePage)
      .subscribe((isMyProfilePage) => {
        const path = isMyProfilePage
          ? 'my-profile'
          : this.route.snapshot.paramMap.get('urid');
        this.store.dispatch(
          canGoBack({
            goBackRoute: `/user-details/${path}`,
          })
        );
      });
    this.updateType$ = this.store.select(selectUserDetailsUpdateType);
    this.isMyProfilePage$ = this.store.select(selectIsLoadedFromMyProfilePage);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onContinue(updateType: UserUpdateDetailsType) {
    this.store.dispatch(setRequestUpdateType({ updateType }));
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
