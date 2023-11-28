/**
 * https://ngrx.io/guide/router-store/configuration
 * The DefaultRouterStateSerializer cannot be used when serializability runtime checks are enabled.
 * If you want to use runtime checks to enforce serializability of your state and actions,
 * you can configure RouterStoreModule to use the MinimalRouterStateSerializer or implement
 * a custom router state serializer. This also applies to Ivy with immutability runtime checks.
 */

import { Params, RouterStateSnapshot } from '@angular/router';
import { RouterStateSerializer } from '@ngrx/router-store';

export interface RouterStateUrl {
  url: string;
  params: Params;
  queryParams: Params;
}

export class CustomSerializer implements RouterStateSerializer<RouterStateUrl> {
  serialize(routerState: RouterStateSnapshot): RouterStateUrl {
    let route = routerState.root;

    while (route.firstChild) {
      route = route.firstChild;
    }

    const {
      url,
      root: { queryParams },
    } = routerState;
    const { params } = route;

    // Only return an object including the URL, params and query params
    // instead of the entire snapshot
    return { url, params, queryParams };
  }
}
