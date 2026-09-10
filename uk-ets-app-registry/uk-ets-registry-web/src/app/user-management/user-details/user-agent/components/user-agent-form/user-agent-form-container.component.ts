import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { CountryCodeModel } from '@shared/countries/country-code.model';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { combineLatest, Observable, take } from 'rxjs';
import {
  UserAgentActions,
  selectAgentType,
  selectCallerExtras,
  selectCallerRoute,
  selectPublicAgentDetails,
} from '@user-agent/store';
import { PublicAgentDetails, UserAgentUpdateRequest } from '@user-agent/model';
import { AgentType } from '@user-management/user-details/model';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { selectUserDetails } from '@user-management/user-details/store/reducers';

@Component({
  selector: 'app-user-agent-form-container',
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
    <app-user-agent-form
      [agentType]="agentType$ | async"
      [agentDetails]="agentDetails$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (agentDetailsOutput)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-user-agent-form> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserAgentFormContainerComponent implements OnInit {
  user$ = this.store.select(selectUserDetails);
  agentType$: Observable<AgentType> = this.store.select(selectAgentType);
  agentDetails$: Observable<PublicAgentDetails> = this.store.select(
    selectPublicAgentDetails
  ) as Observable<PublicAgentDetails>;
  countries$: Observable<IUkOfficialCountry[]> =
    this.store.select(selectAllCountries);
  countryCodes$: Observable<CountryCodeModel[]> =
    this.store.select(selectCountryCodes);
  callerRoute$: Observable<string> = this.store.select(selectCallerRoute);
  callerExtras$: Observable<unknown> = this.store.select(selectCallerExtras);

  constructor(private store: Store) {}

  ngOnInit() {
    combineLatest([this.callerRoute$, this.callerExtras$])
      .pipe(take(1))
      .subscribe(([route, extras]) => {
        this.store.dispatch(
          canGoBack({
            goBackRoute: route,
            extras: extras,
          })
        );
      });
  }

  onContinue(value: UserAgentUpdateRequest) {
    this.store.dispatch(
      UserAgentActions.requestUserAgentUpdate({
        request: value,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
