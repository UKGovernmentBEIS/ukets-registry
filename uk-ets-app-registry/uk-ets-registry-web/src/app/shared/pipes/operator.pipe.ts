import { Pipe, PipeTransform } from '@angular/core';
import {
  AircraftOperator,
  Installation,
  MaritimeOperator,
  Operator,
} from '../model/account/operator';

@Pipe({
  name: 'installation',
  pure: true,
})
export class InstallationPipe implements PipeTransform {
  transform(value: Operator): Installation {
    return value as Installation;
  }
}

@Pipe({
  name: 'aircraftOperator',
  pure: true,
})
export class AircraftOperatorPipe implements PipeTransform {
  transform(value: Operator): AircraftOperator {
    return value as AircraftOperator;
  }
}

@Pipe({
  name: 'maritimeOperator',
  pure: true,
})
export class MaritimeOperatorPipe implements PipeTransform {
  transform(value: Operator): MaritimeOperator {
    return value as MaritimeOperator;
  }
}
