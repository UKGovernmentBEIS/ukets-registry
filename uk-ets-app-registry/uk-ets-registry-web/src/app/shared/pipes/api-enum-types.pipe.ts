import { Pipe, PipeTransform } from '@angular/core';
import { TRANSACTION_TYPES_VALUES } from '@shared/model/transaction';
import { ApiEnumTypes } from '@shared/model';

/**
 *  Renders labels, descriptions for ApiEnumTypes from Records default the label is retrieve
 * @param value an enumeration value e.g TransactionType.IssuanceOfAllowances
 * @param enumType one of ApiEnumTypes
 * @param attr record attribute, by default gets the label
 * {{ transactionTaskDetails.trType | ApiEnumTypes.TransactionType }}
 * {{ transactionTaskDetails.trType | ApiEnumTypes.TransactionType: 'description' }}
 */
@Pipe({
  name: 'apiEnumTypes',
})

// TODO add eventPipe, userStatus and all the api enums here.
export class ApiEnumTypesPipe implements PipeTransform {
  transform(value: any, enumType: ApiEnumTypes, attr = 'label'): string {
    if (isEmpty(value)) {
      return null;
    }
    if (value) {
      switch (enumType) {
        case ApiEnumTypes.TransactionType:
          return TRANSACTION_TYPES_VALUES[value][attr]['defaultLabel'];
        default:
          return value;
      }
    }
  }
}

function isEmpty(value: any): boolean {
  return value == null || value === '' || value !== value;
}
