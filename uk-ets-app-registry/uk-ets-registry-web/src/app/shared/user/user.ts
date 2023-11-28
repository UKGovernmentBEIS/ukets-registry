import 'reflect-metadata';
import { UkDate } from '../model/uk-date';
import { Status } from '@shared/model/status';

/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

export type UserStatus =
  | 'ACTIVE'
  | 'REGISTERED'
  | 'VALIDATED'
  | 'ENROLLED'
  | 'UNENROLLEMENT_PENDING'
  | 'UNENROLLED'
  | 'SUSPENDED'
  | 'REMOVED'
  | 'REJECTED'
  | 'REQUESTED'
  | 'DEACTIVATION_PENDING'
  | 'DEACTIVATED';

export const userStatusMap: Record<UserStatus, Status> = {
  ACTIVE: { color: 'green', label: 'Active' },
  REGISTERED: { color: 'blue', label: 'Registered' },
  VALIDATED: { color: 'yellow', label: 'Validated' },
  ENROLLED: { color: 'green', label: 'Enrolled' },
  UNENROLLEMENT_PENDING: { color: 'yellow', label: 'Unenrollment pending' },
  UNENROLLED: { color: 'grey', label: 'Unenrolled' },
  SUSPENDED: { color: 'red', label: 'Suspended' },
  REMOVED: { color: 'red', label: 'Removed' },
  REJECTED: { color: 'red', label: 'Rejected' },
  REQUESTED: { color: 'grey', label: 'Requested' },
  DEACTIVATION_PENDING: { color: 'yellow', label: 'Deactivation pending' },
  DEACTIVATED: { color: 'red', label: 'Deactivated' },
};

export interface IUser {
  emailAddress: string;
  emailAddressConfirmation: string;
  userId: string;
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  alsoKnownAs: string;
  buildingAndStreet: string;
  buildingAndStreetOptional: string;
  buildingAndStreetOptional2: string;
  postCode: string;
  townOrCity: string;
  stateOrProvince: string;
  country: string;
  birthDate: UkDate;
  countryOfBirth: string;
  workCountryCode: string;
  workPhoneNumber: string;
  workEmailAddress: string;
  workEmailAddressConfirmation: string;
  workBuildingAndStreet: string;
  workBuildingAndStreetOptional: string;
  workBuildingAndStreetOptional2: string;
  workTownOrCity: string;
  workStateOrProvince: string;
  workPostCode: string;
  workCountry: string;
  urid: string;
  state: string;
  status: UserStatus;
  memorablePhrase: string;
}

export class User implements IUser {
  emailAddress = '';
  emailAddressConfirmation = '';
  userId = '';
  username = '';
  password = '';
  firstName = '';
  lastName = '';
  alsoKnownAs = '';
  buildingAndStreet = '';
  buildingAndStreetOptional = '';
  buildingAndStreetOptional2 = '';
  postCode = '';
  townOrCity = '';
  stateOrProvince = '';
  country = '';
  birthDate = { day: null, month: null, year: null };
  countryOfBirth = '';
  workCountryCode = '';
  workPhoneNumber = '';
  workEmailAddress = '';
  workEmailAddressConfirmation = '';
  workBuildingAndStreet = '';
  workBuildingAndStreetOptional = '';
  workBuildingAndStreetOptional2 = '';
  workTownOrCity = '';
  workStateOrProvince = '';
  workPostCode = '';
  workCountry = '';
  urid = '';
  state = 'REGISTERED';
  status = 'REGISTERED' as UserStatus;
  memorablePhrase = '';

  static updatePartially(user: User, partialUser: Partial<User>): IUser {
    return User.decode({ ...user, ...partialUser });
  }

  static fromJSON(json: IUser | string): User {
    if (typeof json === 'string') {
      return JSON.parse(json, User.reviver);
    } else if (!json) {
      return new User();
    } else {
      return Object.assign(new User(), json);
    }
  }

  static reviver(key: string, value: any): any {
    return key === '' ? User.fromJSON(value) : value;
  }

  static decode(json: Partial<IUser>): IUser {
    const user = Object.create(User.prototype);
    return Object.assign(user, json, {});
  }

  toJSON(): IUser {
    return Object.assign({}, this, {});
  }
}
