import { Pipe, PipeTransform } from '@angular/core';
import { KeycloakUser } from '../user';

@Pipe({
  name: 'keycloakUserDisplayName',
})
export class KeycloakUserDisplayNamePipe implements PipeTransform {
  transform(user: KeycloakUser): string {
    return user.attributes
      ? user.attributes.alsoKnownAs && user.attributes.alsoKnownAs[0].length > 0
        ? `${user.attributes.alsoKnownAs[0]}`
        : `${user.firstName} ${user.lastName}`
      : '';
  }
}
