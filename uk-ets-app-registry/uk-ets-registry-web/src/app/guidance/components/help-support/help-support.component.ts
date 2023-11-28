import { Component, OnInit } from '@angular/core';
import {
  selectRegistryConfigurationProperty
} from '@shared/shared.selector';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-guidance-help-support',
  templateUrl: './help-support.component.html',
})
export class HelpSupportComponent implements OnInit {
  etsRegistryHelpEmail$: Observable<any>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.etsRegistryHelpEmail$ = this.store.select(
      selectRegistryConfigurationProperty,
      {
        property: 'mail.etrAddress',
      }
    );

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance`,
      })
    );
  }
}
