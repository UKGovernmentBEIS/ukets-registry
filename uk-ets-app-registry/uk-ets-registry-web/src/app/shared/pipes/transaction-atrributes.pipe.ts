import { Pipe, PipeTransform } from '@angular/core';

/**
 * A pipe to return a transaction attribute value given a key
 */
@Pipe({
  name: 'transactionAtrributes',
})
export class TransactionAtrributesPipe implements PipeTransform {
  transform(attributes: string, key: string): string {
    if (isEmpty(attributes) || isEmpty(key)) {
      return '';
    }

    if (attributes) {
      const jsonAttributes = JSON.parse(attributes);

      return jsonAttributes[key];
    }
  }
}

function isEmpty(value: any): boolean {
  return value == null || value === '' || value !== value;
}
