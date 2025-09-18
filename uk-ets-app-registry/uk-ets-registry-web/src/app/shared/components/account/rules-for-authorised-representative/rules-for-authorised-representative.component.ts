import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import {
  selectIsOHAOrAOHAorMOHA,
  selectMaxNumberOfARs,
  selectMinNumberOfARs,
} from '@account-opening/account-opening.selector';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-rules-for-authorised-representative',
  templateUrl: './rules-for-authorised-representative.component.html',
})
export class RulesForAuthorisedRepresentativeComponent {
  constructor(private store: Store) {}

  maxNumberOfARs$: Observable<number> = this.store.select(selectMaxNumberOfARs);

  minNumberOfARs$: Observable<number> = this.store.select(selectMinNumberOfARs);

  showSurrenderText$ = this.store.select(selectIsOHAOrAOHAorMOHA);
}
