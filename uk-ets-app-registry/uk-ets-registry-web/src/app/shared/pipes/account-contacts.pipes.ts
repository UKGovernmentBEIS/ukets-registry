import { Pipe, PipeTransform } from '@angular/core';
import {
  MetsContactOperatorType,
  MetsContactType,
  RegistryContactType,
} from '@registry-web/shared/model/account/account-contacts.interface';

@Pipe({
  name: 'metsContactType',
  pure: true,
  standalone: true,
})
export class MetsContactTypePipe implements PipeTransform {
  transform(contactType: MetsContactType): string {
    switch (contactType) {
      case 'PRIMARY':
        return 'Primary contact';
      case 'SECONDARY':
        return 'Secondary contact';
      case 'SERVICE':
        return 'Service contact';
      case 'FINANCIAL':
        return 'Financial contact';
      default:
        return '';
    }
  }
}

@Pipe({
  name: 'metsContactOperatorType',
  pure: true,
  standalone: true,
})
export class MetsContactOperatorTypePipe implements PipeTransform {
  transform(operatorType: MetsContactOperatorType): string {
    switch (operatorType) {
      case 'OPERATOR_ADMIN':
        return 'Operator Admin';
      case 'OPERATOR':
        return 'Operator';
      case 'CONSULTANT_AGENT':
        return 'Consultant/Agent';
      case 'EMITTER':
        return 'Emitter';
      default:
        return '';
    }
  }
}

@Pipe({
  name: 'registryContactType',
  pure: true,
  standalone: true,
})
export class RegistryContactTypePipe implements PipeTransform {
  transform(contactType: RegistryContactType): string {
    switch (contactType) {
      case 'PRIMARY':
        return 'Primary contact';
      case 'ALTERNATIVE':
        return 'Alternative Primary contact';
      default:
        return '';
    }
  }
}
