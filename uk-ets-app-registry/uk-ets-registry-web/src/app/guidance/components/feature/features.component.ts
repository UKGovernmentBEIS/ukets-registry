import { Component, OnInit } from '@angular/core';
import { selectConfigurationRegistry } from '@shared/shared.selector';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { getConfigurationValue } from '@shared/shared.util';
import { Configuration } from '@shared/configuration/configuration.interface';

@Component({
  selector: 'app-guidance-features',
  templateUrl: './feature.components.html',
})
export class FeaturesComponent implements OnInit {
  serviceDeskEmail: string;

  configuration$: Observable<Configuration[]> = this.store.select(
    selectConfigurationRegistry
  );

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.configuration$.pipe(take(1)).subscribe((configuration) => {
      this.serviceDeskEmail = getConfigurationValue(
        'mail.etrAddress',
        configuration
      );
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance`,
      })
    );
  }
}
