import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthoritySettingState } from '@authority-setting/reducer';
import { Store } from '@ngrx/store';
import { selectState } from '@authority-setting/reducer/authority-setting.selector';
import { fetchEnrolledUser } from '@authority-setting/action';
import { canGoBack } from '@shared/shared.action';
import { AuthoritySettingRoutePathsModel } from '@authority-setting/model/authority-setting-route-paths.model';

@Component({
  selector: 'app-set-authority-user-form-container',
  template: `
    <app-set-authority-user-form
      [state]="authoritySettingState$ | async"
      (submitEnrolledUserId)="continue($event)"
    >
    </app-set-authority-user-form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SetAuthorityUserFormContainerComponent implements OnInit {
  authoritySettingState$: Observable<AuthoritySettingState>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.authoritySettingState$ = this.store.select(selectState);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION}`
      })
    );
  }
  continue(urid: string) {
    this.store.dispatch(
      fetchEnrolledUser({
        urid
      })
    );
  }
}
