import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { FormRadioOption } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AuthApiService } from '../../../../auth/auth-api.service';

@Injectable()
export class UpdateTypesResolver {
  options: FormRadioOption[] = [
    {
      label: 'Add representative',
      value: AuthorisedRepresentativesUpdateType.ADD,
      enabled: true,
    },
    {
      label: 'Remove representative',
      value: AuthorisedRepresentativesUpdateType.REMOVE,
      enabled: true,
    },
    {
      label: 'Replace representative',
      value: AuthorisedRepresentativesUpdateType.REPLACE,
      enabled: true,
    },
    {
      label: 'Change permissions',
      value: AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS,
      enabled: true,
    },
  ];

  constructor(private authApiService: AuthApiService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<FormRadioOption[]> {
    return this.authApiService
      .hasScope('urn:uk-ets-registry-api:actionForSeniorAdmin')
      .pipe(
        map((isAdmin) => {
          if (!isAdmin) {
            return this.options;
          } else {
            const extendedOptions = Object.assign([], this.options);
            extendedOptions.push(
              {
                label: 'Suspend representative',
                value: AuthorisedRepresentativesUpdateType.SUSPEND,
                enabled: true,
              },
              {
                label: 'Restore representative',
                value: AuthorisedRepresentativesUpdateType.RESTORE,
                enabled: true,
              }
            );
            return extendedOptions;
          }
        })
      );
  }
}
