import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { TransactionManagementService } from '../service/transaction-management.service';
import { FiltersDescriptor } from './transaction-list.model';

@Injectable()
export class FiltersDescriptorResolver {
  constructor(private service: TransactionManagementService) {}
  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<FiltersDescriptor> {
    return this.service.getFiltersPermissions();
  }
}
