import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectBooleanConfigurationProperty,
  selectRegistryConfigurationProperty,
} from '@shared/shared.selector';

@Component({
  selector: 'app-dashboard-container',
  template: `
    <app-dashboard
      [systemAdministrationEnabled]="systemAdministrationEnabled$ | async"
    ></app-dashboard>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardContainerComponent implements OnInit {
  systemAdministrationEnabled$: Observable<boolean>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.systemAdministrationEnabled$ = this.store.select(
      selectBooleanConfigurationProperty,
      {
        property: 'system.administration.enabled',
      }
    );
  }
}
