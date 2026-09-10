import { NavigationExtras } from '@angular/router';
import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { UserCrcInfo, UserCrcUpdateRequest } from '@user-crc/model';

export const UserCrcActions = createActionGroup({
  source: 'UserCrc',
  events: {
    'Load User Crc Details': props<{
      crcInfo: UserCrcInfo;
      caller: {
        route: string;
        extras?: NavigationExtras;
      };
    }>(),
    'Request User Crc Update': props<{ request: UserCrcUpdateRequest }>(),
    'Request User Crc Update Success': emptyProps(),
    'Request User Crc Update Failure': props<{ error: unknown }>(),
    'Clear User Crc Details': emptyProps(),
  },
});
