import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { Observable } from 'rxjs';
import { KeycloakUser } from '@shared/user';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-user-update-container',
  template: `<app-feature-header-wrapper>
      <app-user-header
        [user]="user$ | async"
        [userHeaderVisibility]="true"
        [userHeaderActionsVisibility]="false"
        [showBackToList]="false"
        [showRequestUpdate]="false"
      >
      </app-user-header>
    </app-feature-header-wrapper>
    <router-outlet></router-outlet> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDetailsUpdateContainerComponent implements OnInit {
  user$: Observable<KeycloakUser>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.user$ = this.store.select(selectUserDetails);
  }
}
