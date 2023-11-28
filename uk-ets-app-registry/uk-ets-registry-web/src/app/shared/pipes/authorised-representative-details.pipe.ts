import { Pipe, PipeTransform } from '@angular/core';
import { AuthorisedRepresentative } from '@shared/model/account';

@Pipe({
  name: 'authorisedRepresentativeDetails',
})
export class AuthorisedRepresentativeDetailsPipe implements PipeTransform {
  transform(ar: AuthorisedRepresentative): string {
    return ar
      ? ar.user?.alsoKnownAs && ar.user.alsoKnownAs.length > 0
        ? `${ar.user.alsoKnownAs} (User ID: ${ar.urid})`
        : `${ar.firstName} ${ar.lastName} (User ID: ${ar.urid})`
      : '';
  }
}
