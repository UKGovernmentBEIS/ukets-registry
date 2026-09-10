import { NavigationExtras } from '@angular/router';
import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { UserAgentInfo, UserAgentUpdateRequest } from '@user-agent/model';

export const UserAgentActions = createActionGroup({
  source: 'UserAgent',
  events: {
    'Load User Agent Details': props<{
      agentInfo: UserAgentInfo;
      caller: {
        route: string;
        extras?: NavigationExtras;
      };
    }>(),
    'Request User Agent Update': props<{ request: UserAgentUpdateRequest }>(),
    'Request User Agent Update Success': emptyProps(),
    'Request User Agent Update Failure': props<{ error: unknown }>(),
    'Clear User Agent Details': emptyProps(),
  },
});
