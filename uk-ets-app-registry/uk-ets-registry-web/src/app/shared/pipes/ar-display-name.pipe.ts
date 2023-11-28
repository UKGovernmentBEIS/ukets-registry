import { Pipe, PipeTransform } from '@angular/core';
import { AuthorisedRepresentative } from '@shared/model/account';

@Pipe({
  name: 'arDisplayName',
})
export class ArDisplayNamePipe implements PipeTransform {
  transform(ar: AuthorisedRepresentative): string {
    return ar
      ? ar.user.alsoKnownAs && ar.user.alsoKnownAs.length > 0
        ? `${ar.user.alsoKnownAs}`
        : `${ar.user.firstName} ${ar.user.lastName}`
      : '';
  }
}
