import { Pipe, PipeTransform } from '@angular/core';
import { RequestType } from '@task-management/model';

/**
 * A pipe to get the TAL task description
 */
@Pipe({
  name: 'trustedAccountTaskDescription'
})
export class TrustedAccountTaskDescriptionPipe implements PipeTransform {
  transform(value: RequestType): string {
    switch (value) {
      case RequestType.ADD_TRUSTED_ACCOUNT_REQUEST:
        return 'The following account will be added to the trusted account list';
        break;
      case RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST:
        return 'The following account(s) will be removed from the trusted account list';
        break;
      default:
        break;
    }
  }
}
