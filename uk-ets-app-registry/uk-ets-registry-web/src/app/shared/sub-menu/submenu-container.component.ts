import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { MenuItem } from '@shared/model/navigation-menu';
import {
  selectSubMenuActive,
  selectVisibleSubMenuItems
} from '@shared/shared.selector';

@Component({
  selector: 'app-submenu-container',
  template: `
    <app-sub-menu
      [subMenuItems]="subMenuItems$ | async"
      [subMenuActive]="subMenuActive$ | async"
    ></app-sub-menu>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubmenuContainerComponent implements OnInit {
  subMenuItems$: Observable<MenuItem[]>;
  subMenuActive$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.subMenuItems$ = this.store.select(selectVisibleSubMenuItems);
    this.subMenuActive$ = this.store.select(selectSubMenuActive);
  }
}
