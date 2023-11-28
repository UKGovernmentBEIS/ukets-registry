import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { cancelAuthoritySetting } from '@authority-setting/action';

@Component({
  selector: 'app-cancel-update-allocation-status',
  template: `
    <app-cancel-update-request
      notification="Are you sure you want to cancel the set/unset authority
      user request and return back to the registry administration page?"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `
})
export class CancelAuthoritySettingComponent {
  constructor(private store: Store) {}

  onCancel() {
    this.store.dispatch(cancelAuthoritySetting());
  }
}
