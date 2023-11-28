import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  clearAuthoritySettingState,
  startAuthoritySettingWizard
} from '@authority-setting/action';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-registry-administration-container',
  template: `
    <app-registry-administration (setUserAsAuthority)="startWizard()">
    </app-registry-administration>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegistryAdministrationContainerComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: null
      })
    );
    this.store.dispatch(clearAuthoritySettingState());
  }

  startWizard() {
    this.store.dispatch(startAuthoritySettingWizard());
  }
}
