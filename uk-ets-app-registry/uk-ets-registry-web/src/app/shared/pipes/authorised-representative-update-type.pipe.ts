import { Pipe, PipeTransform } from '@angular/core';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';

/**
 * A pipe to format the update type of the authorised representatives
 */
@Pipe({
  name: 'arUpdateType',
})
export class AuthorisedRepresentativeUpdateTypePipe implements PipeTransform {
  transform(value: AuthorisedRepresentativesUpdateType): string {
    switch (value) {
      case AuthorisedRepresentativesUpdateType.ADD:
        return 'Add representative';
      case AuthorisedRepresentativesUpdateType.REMOVE:
        return 'Remove representative';
      case AuthorisedRepresentativesUpdateType.REPLACE:
        return 'Replace representative';
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
        return 'Change permissions';
      case AuthorisedRepresentativesUpdateType.RESTORE:
        return 'Restore representative';
      case AuthorisedRepresentativesUpdateType.SUSPEND:
        return 'Suspend representative';
    }
  }
}
