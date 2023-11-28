import { Pipe, PipeTransform } from '@angular/core';
import {
  AccountHolder,
  Organisation,
  Individual,
  IndividualDetails,
  Government,
} from '../model/account/account-holder';

@Pipe({
  name: 'organisation',
  pure: true,
})
export class OrganisationPipe implements PipeTransform {
  transform(value: AccountHolder): Organisation {
    return value as Organisation;
  }
}

@Pipe({
  name: 'individual',
  pure: true,
})
export class IndividualPipe implements PipeTransform {
  transform(value: AccountHolder): Individual {
    return value as Individual;
  }
}

@Pipe({
  name: 'government',
  pure: true,
})
export class GovernmentPipe implements PipeTransform {
  transform(value: AccountHolder): Government {
    return value as Government;
  }
}

@Pipe({
  name: 'individualFullName',
  pure: true,
})
export class IndividualFullNamePipe implements PipeTransform {
  transform(value: IndividualDetails | any): string {
    return value?.firstName && value?.lastName
      ? `${value.firstName} ${value.lastName}`
      : value?.name;
  }
}

@Pipe({
  name: 'individualFirstAndMiddleNames',
  pure: true,
})
export class IndividualFirstAndMiddleNamesPipe implements PipeTransform {
  transform(value: IndividualDetails): string {
    return value?.firstName ? value.firstName : value?.name;
  }
}
