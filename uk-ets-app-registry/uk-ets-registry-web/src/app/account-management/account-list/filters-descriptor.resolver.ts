import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { FiltersDescriptor } from './account-list.model';
import { AccountApiService } from '../service/account-api.service';

@Injectable()
export class FiltersDescriptorResolver {
  constructor(private service: AccountApiService) {}
  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<FiltersDescriptor> {
    return this.service.getFiltersPermissions();
  }
}
