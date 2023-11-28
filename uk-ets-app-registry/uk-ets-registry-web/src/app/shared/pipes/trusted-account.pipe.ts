import { Pipe, PipeTransform } from '@angular/core';
import { AcquiringAccountInfo } from '@shared/model/transaction';

/**
 * A pipe to find if the accountIsTrusted
 */
@Pipe({
  name: 'trustedAccount',
})
export class TrustedAccountPipe implements PipeTransform {
  transform(value: AcquiringAccountInfo): string {
    if (!value || (value && !value.fullIdentifier)) {
      return 'No acquiring account is available for this transaction. Please contact the Help Desk.';
    } else if (value && !!value.trusted) {
      return 'Trusted account';
    } else if (value && !value.trusted) {
      return 'Account not in the trusted account list';
    } else {
      return '';
    }
  }
}
