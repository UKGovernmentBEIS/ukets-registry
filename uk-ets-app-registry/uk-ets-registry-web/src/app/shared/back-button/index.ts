import { Params } from '@angular/router';

export interface GoBackNavigationExtras {
  queryParams?: Params | null;
  skipLocationChange?: boolean;
}
