import { Component, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectRegistrationConfigurationProperty,
  selectRegistryConfigurationProperty,
} from '@shared/shared.selector';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-guidance-surrender-obligation',
  templateUrl: './surrender-obligation.component.html',
  styleUrls: ['./surrender-obligation.component.css'],
})
export class SurrenderObligationComponent implements OnInit {
  etsRegistryHelpEmail$: Observable<any>;
  environment = environment;

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
